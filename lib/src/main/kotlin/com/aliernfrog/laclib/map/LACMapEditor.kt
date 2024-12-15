package com.aliernfrog.laclib.map

import com.aliernfrog.laclib.data.LACMapDownloadableMaterial
import com.aliernfrog.laclib.data.LACMapObject
import com.aliernfrog.laclib.data.LACMapObjectFilter
import com.aliernfrog.laclib.data.LACMapOption
import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOptionType
import com.aliernfrog.laclib.enum.LACMapType
import com.aliernfrog.laclib.util.ILLEGAL_ROLE_CHARS
import com.aliernfrog.laclib.util.LACLibUtil
import com.aliernfrog.laclib.util.extension.matchesLine
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * Initializes a LAC map editor instance.
 * @param content Content of the map
 * @param loadInParallelBatchSize Batch size when loading the map in parallel, pass 0 or less to disable parallel loading
 * @param onDebugLog [Unit] to invoke when debug log is received
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class LACMapEditor(
    content: String,
    loadInParallelBatchSize: Int = 10,
    private val onDebugLog: (String) -> Unit = {}
) {
    private var mapLines = content.split("\n").toMutableList()
    var serverName: String? = null
    var mapType: LACMapType? = null
    var mapRoles: MutableList<String>? = null
    var mapOptions = mutableListOf<LACMapOption>()
    var replaceableObjects = mutableListOf<LACMapObject>()
    var downloadableMaterials = mutableListOf<LACMapDownloadableMaterial>()

    private var serverNameLine: Int? = null
    private var mapTypeLine: Int? = null
    private var mapRolesLine: Int? = null

    @Suppress("SpellCheckingInspection")
    @Deprecated("This is a typo and will be removed in next major release. Use replaceableObjects instead.", ReplaceWith("replaceableObjects"))
    var replacableObjects: MutableList<LACMapObject>
        get() = replaceableObjects
        set(value) { replaceableObjects = value }

    init {
        val materialsLookupMap: MutableMap<String, MutableList<LACMapObject>> = mutableMapOf()

        fun processLine(index: Int, line: String) {
            when (val type = LACLibUtil.getEditorLineType(line)) {
                LACMapLineType.SERVER_NAME -> {
                    serverName = type.getValue(line)
                    serverNameLine = index
                }
                LACMapLineType.MAP_TYPE -> {
                    mapType = LACMapType.entries[type.getValue(line).toInt()]
                    mapTypeLine = index
                }
                LACMapLineType.ROLES_LIST -> {
                    mapRoles = type.getValue(line).removeSuffix(",").split(",").toMutableList()
                    mapRolesLine = index
                }
                LACMapLineType.OPTION_NUMBER -> mapOptions.add(LACMapOption(
                    type = LACMapOptionType.NUMBER,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OPTION_BOOLEAN -> mapOptions.add(LACMapOption(
                    type = LACMapOptionType.BOOLEAN,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OPTION_SWITCH -> mapOptions.add(LACMapOption(
                    type = LACMapOptionType.SWITCH,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OBJECT -> {
                    val mapObject = LACMapObject(
                        line = line,
                        lineNumber = index,
                        canReplaceWith = LACLibUtil.findReplacementForObject(line)
                    )
                    if (mapObject.canReplaceWith != null) replaceableObjects.add(mapObject)

                    if (line.contains(" material{")) {
                        val regex = Regex("material\\{(.+?)\\}")
                        val matchResult = regex.find(line)
                        val materialProps = matchResult?.groups?.get(1)?.value
                        val fileName = materialProps?.split(",")?.get(0)
                        if (fileName != null) {
                            val list = materialsLookupMap[fileName] ?: mutableListOf()
                            list.add(mapObject)
                            materialsLookupMap[fileName] = list
                        }
                    }
                }
                LACMapLineType.DOWNLOADABLE_MATERIAL -> {
                    val url = type.getValue(line)
                    downloadableMaterials.add(LACMapDownloadableMaterial(
                        url = url,
                        name = url.split("/").last(),
                        usedBy = mutableListOf()
                    ))
                }
                else -> {
                    onDebugLog("unhandled line type: $type, line at $index: $line")
                }
            }
        }

        if (loadInParallelBatchSize > 0) {
            IntStream.range(0, mapLines.size)
                .parallel()
                .mapToObj { index -> Pair(index, mapLines[index]) }
                .parallel()
                .collect(Collectors.groupingBy { it.first / loadInParallelBatchSize })
                .forEach { (_, batch) ->
                    batch.parallelStream().forEach { (index, line) ->
                        processLine(index, line)
                        onDebugLog("loaded line $index: $line")
                    }
                }
        } else mapLines.forEachIndexed { index, line ->
            processLine(index, line)
        }

        downloadableMaterials.forEachIndexed { index, material ->
            downloadableMaterials[index] = material.copy(
                usedBy = materialsLookupMap[material.name] ?: mutableListOf()
            )
        }
        materialsLookupMap.clear()
    }

    /**
     * Replaces replaceable objects.
     * @return count of replaced objects
     */
    fun replaceOldObjects(): Int {
        val replacedCount = replaceableObjects.size
        replaceableObjects.forEach { mapObject ->
            val split = mapObject.line.split(":").toMutableList()
            val replacement = mapObject.canReplaceWith!!
            if (split.size < 4) split.add(3, "1.0,1.0,1.0")
            split[0] = replacement.replaceName
            if (replacement.replaceScale != null) split[3] = replacement.replaceScale
            if (replacement.replaceColor != null) split.add(replacement.replaceColor)
            mapLines[mapObject.lineNumber] = split.joinToString(":")
        }
        replaceableObjects.clear()
        return replacedCount
    }

    /**
     * Returns a list of object lines matching given [filter].
     */
    fun getObjectsMatchingFilter(filter: LACMapObjectFilter): List<String> {
        return mapLines.filter { line ->
            filter.matchesLine(line)
        }
    }

    /**
     * Removes objects matching [filter] from the map.
     * @return count of removed objects
     */
    fun removeObjectsMatchingFilter(filter: LACMapObjectFilter): Int {
        val filtered = mapLines.filter { line ->
            !filter.matchesLine(line)
        }.toMutableList()
        val removedObjects = mapLines.size - filtered.size
        mapLines = filtered
        return removedObjects
    }

    /**
     * Adds [role] to role list.
     * @param onIllegalChar [Unit] to invoke when [role] contains an illegal character
     * @param onSuccess [Unit] to invoke when [role] is added to role list
     */
    fun addRole(
        role: String,
        onIllegalChar: (char: String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val illegalChar = ILLEGAL_ROLE_CHARS.find { role.contains(it) }
        if (illegalChar != null) return onIllegalChar(illegalChar)
        mapRoles?.add(role)
        onSuccess()
    }

    /**
     * Removes [role] from role list.
     */
    fun deleteRole(role: String) {
        mapRoles?.remove(role)
    }

    /**
     * Removes downloadable material and removes it from all the objects using it.
     * @param url URL of the material
     * @return Count of objects that used the material, null if material was not found
     */
    fun removeDownloadableMaterial(url: String): Int? {
        val material = downloadableMaterials.find { it.url == url } ?: return null
        val usedBy = material.usedBy
        usedBy.forEach { mapObject ->
            val objectIndex = mapLines.indexOf(mapObject.line)
            // "}" not being escaped causes crash on Android devices
            val materialRemoved = mapObject.line.replace("material\\{.*,.*\\}".toRegex(), "")
            if (objectIndex != -1) mapLines[objectIndex] = materialRemoved
        }
        mapLines.removeIf { line ->
            val type = LACLibUtil.getEditorLineType(line)
            type == LACMapLineType.DOWNLOADABLE_MATERIAL && line.contains(material.url)
        }
        downloadableMaterials.remove(material)
        return usedBy.size
    }

    /**
     * Returns the current content.
     */
    fun getCurrentContent(): String {
        return mapLines.joinToString("\n")
    }

    /**
     * Applies changes to map content and returns the new content.
     */
    fun applyChanges(): String {
        if (serverNameLine != null && serverName != null) {
            val applied = LACMapLineType.SERVER_NAME.setValue(serverName!!)
            onDebugLog("setting server name ($serverNameLine) ${mapLines[serverNameLine!!]} to -> $applied")
            mapLines[serverNameLine!!] = applied
        }
        if (mapTypeLine != null && mapType != null) {
            val applied = LACMapLineType.MAP_TYPE.setValue(mapType!!.index.toString())
            onDebugLog("setting map type ($mapTypeLine) ${mapLines[mapTypeLine!!]} to -> $applied")
            mapLines[mapTypeLine!!] = applied
        }
        if (mapRolesLine != null && mapRoles != null) {
            val applied = LACMapLineType.ROLES_LIST.setValue(mapRoles!!.joinToString(",").plus(","))
            onDebugLog("setting map roles ($mapRolesLine) ${mapLines[mapRolesLine!!]} to -> $applied")
            mapLines[mapRolesLine!!] = applied
        }
        mapOptions.forEach { option ->
            val applied = LACMapLineType.OPTION_GENERAL.setValue(option.value, option.label)
            onDebugLog("setting map option ${option.label}:${option.value} (${option.line}) to -> $applied")
            mapLines[option.line] = applied
        }
        return getCurrentContent()
    }
}