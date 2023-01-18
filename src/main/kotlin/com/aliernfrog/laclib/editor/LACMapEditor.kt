package com.aliernfrog.laclib.editor

import com.aliernfrog.laclib.data.LACMapObject
import com.aliernfrog.laclib.data.LACMapObjectFilter
import com.aliernfrog.laclib.data.LACMapOption
import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOptionType
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
    val mapRoles = mutableListOf<String>()
    val mapOptions = mutableListOf<LACMapOption>()
    val replacableObjects = mutableListOf<LACMapObject>()

    private var serverNameLine: Int? = null
    private var mapTypeLine: Int? = null
    private var mapRolesLine: Int? = null

    init {
        mapLines.forEachIndexed { index, line ->
            when (val type = LACLibUtil.getEditorLineType(line)) {
                LACMapLineType.SERVER_NAME -> {
                    serverNameLine = index
                }
                LACMapLineType.MAP_TYPE -> {
                    mapTypeLine = index
                }
                LACMapLineType.ROLES_LIST -> {
                    mapRoles.addAll(type.getValue(line).removeSuffix(",").split(",").toList())
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
     * Removes objects matching [filter] from the map.
     * @return count of removed objects
     */
    fun removeMatchingObjects(filter: LACMapObjectFilter): Int {
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
        if (illegalChar != null) onIllegalChar(illegalChar)
        mapRoles.add(role)
        onSuccess()
    }

    /**
     * Removes [role] from role list.
     */
    fun deleteRole(role: String) {
        mapRoles.remove(role)
    }
}