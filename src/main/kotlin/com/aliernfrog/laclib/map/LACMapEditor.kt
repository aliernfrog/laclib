package com.aliernfrog.laclib.map

import com.aliernfrog.laclib.data.LACMapObject
import com.aliernfrog.laclib.data.LACMapObjectFilter
import com.aliernfrog.laclib.data.LACMapOption
import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOptionType
import com.aliernfrog.laclib.enum.LACMapType
import com.aliernfrog.laclib.util.ILLEGAL_ROLE_CHARS
import com.aliernfrog.laclib.util.LACLibUtil
import com.aliernfrog.laclib.util.extension.matchesLine

/**
 * Initializes a LAC map editor instance.
 * @param content Content of the map
 */
class LACMapEditor(
    content: String
) {
    private var mapLines = content.split("\n").toMutableList()
    var serverName: String? = null
    var mapType: LACMapType? = null
    var mapRoles: MutableList<String>? = null
    var mapOptions = mutableListOf<LACMapOption>()
    var replacableObjects = mutableListOf<LACMapObject>()

    private var serverNameLine: Int? = null
    private var mapTypeLine: Int? = null
    private var mapRolesLine: Int? = null

    init {
        mapLines.forEachIndexed { index, line ->
            when (val type = LACLibUtil.getEditorLineType(line)) {
                LACMapLineType.SERVER_NAME -> {
                    serverName = type.getValue(line)
                    serverNameLine = index
                }
                LACMapLineType.MAP_TYPE -> {
                    mapType = LACMapType.values()[type.getValue(line).toInt()]
                    mapTypeLine = index
                }
                LACMapLineType.ROLES_LIST -> {
                    mapRoles = type.getValue(line).removeSuffix(",").split(",").toMutableList()
                    mapRolesLine = index
                }
                LACMapLineType.OPTION_NUMBER -> mapOptions.add(
                    LACMapOption(
                    type = LACMapOptionType.NUMBER,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OPTION_BOOLEAN -> mapOptions.add(
                    LACMapOption(
                    type = LACMapOptionType.BOOLEAN,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OPTION_SWITCH -> mapOptions.add(
                    LACMapOption(
                    type = LACMapOptionType.SWITCH,
                    label = type.getLabel(line)!!,
                    value = type.getValue(line),
                    line = index
                ))
                LACMapLineType.OBJECT -> {
                    val objectReplacement = LACLibUtil.findReplacementForObject(line)
                    if (objectReplacement != null) replacableObjects.add(LACMapObject(
                        line = line,
                        lineNumber = index,
                        canReplaceWith = objectReplacement
                    ))
                }
                else -> {}
            }
        }
    }

    /**
     * Replaces replacable objects.
     * @return count of replaced objects
     */
    fun replaceOldObjects(): Int {
        val replacedCount = replacableObjects.size
        replacableObjects.forEach { mapObject ->
            val split = mapObject.line.split(":").toMutableList()
            val replacement = mapObject.canReplaceWith!!
            if (split.size < 4) split.add(3, "1.0,1.0,1.0")
            split[0] = replacement.replaceName
            if (replacement.replaceScale != null) split[3] = replacement.replaceScale
            if (replacement.replaceColor != null) split.add(replacement.replaceColor)
            mapLines[mapObject.lineNumber] = split.joinToString(":")
        }
        replacableObjects.clear()
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
     * Returns the current content.
     */
    fun getCurrentContent(): String {
        return mapLines.joinToString("\n")
    }

    /**
     * Applies changes to map content and returns the new content.
     */
    fun applyChanges(): String {
        if (serverNameLine != null && serverName != null)
            mapLines[serverNameLine!!] = LACMapLineType.SERVER_NAME.setValue(serverName!!)
        if (mapTypeLine != null && mapType != null)
            mapLines[mapTypeLine!!] = LACMapLineType.MAP_TYPE.setValue(mapType!!.index.toString())
        if (mapRolesLine != null && mapRoles != null)
            mapLines[mapRolesLine!!] = LACMapLineType.ROLES_LIST.setValue(mapRoles!!.joinToString(",").plus(","))
        mapOptions.forEach { option ->
            mapLines[option.line] = LACMapLineType.OPTION_GENERAL.setValue(option.value, option.label)
        }
        return getCurrentContent()
    }
}