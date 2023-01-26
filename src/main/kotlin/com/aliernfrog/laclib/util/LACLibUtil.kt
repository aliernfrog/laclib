package com.aliernfrog.laclib.util

import com.aliernfrog.laclib.data.LACMapToMerge
import com.aliernfrog.laclib.data.XYZ
import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOldObject
import com.aliernfrog.laclib.util.extension.add
import com.aliernfrog.laclib.util.extension.joinToString

class LACLibUtil {
    companion object {
        /**
         * Gets [LACMapLineType] of [line].
         * @return [LACMapLineType] if known type, [LACMapLineType.UNKNOWN] otherwise
         */
        fun getEditorLineType(line: String): LACMapLineType {
            return LACMapLineType.values().filter { !it.ignoreWhenFiltering }
                .find { it.matches(line) } ?: LACMapLineType.UNKNOWN
        }

        /**
         * Finds [LACMapOldObject] for given [line].
         * @return [LACMapOldObject] if a replacement is found, null otherwise
         */
        fun findReplacementForObject(line: String): LACMapOldObject? {
            val objectName = line.split(":")[0]
            return LACMapOldObject.values().find { it.objectName == objectName }
        }

        /**
         * Processes [mapToMerge] content and returns lines of new content based on options.
         */
        fun processMapToMergeContent(mapToMerge: LACMapToMerge, isBaseMap: Boolean): List<String> {
            val lines = mapToMerge.content.split("\n")
            val filtered = mutableListOf<String>()
            lines.forEach { line ->
                when (val type = getEditorLineType(line)) {
                    LACMapLineType.OBJECT -> {
                        val name = type.getValue(line)
                        val lineToAdd = if (isBaseMap) line else mergeLACObject(mapToMerge, line)
                        if (name == "Spawn_Point_Editor") {
                            if (mapToMerge.mergeSpawnpoints) filtered.add(lineToAdd)
                        } else if (name.startsWith("Checkpoint_Editor")) {
                            if (mapToMerge.mergeRacingCheckpoints) filtered.add(lineToAdd)
                        } else if (name.startsWith("Team_")) {
                            if (mapToMerge.mergeTDMSpawnpoints) filtered.add(lineToAdd)
                        } else {
                            filtered.add(lineToAdd)
                        }
                    }
                    LACMapLineType.VEHICLE -> {
                        val lineToAdd = if (isBaseMap) line else mergeLACObject(mapToMerge, line)
                        filtered.add(lineToAdd)
                    }
                    LACMapLineType.DOWNLOADABLE_MATERIAL -> filtered.add(line)
                    else -> if (isBaseMap) filtered.add(line)
                }
            }
            return filtered
        }

        /**
         * Merges object of [mapToMerge] based on options and returns the new object line.
         */
        fun mergeLACObject(mapToMerge: LACMapToMerge, line: String): String {
            val split = line.split(":").toMutableList()
            val oldPosition = parseAsXYZ(split[1])!!
            val positionToAdd = parseAsXYZ(mapToMerge.mergePosition) ?: XYZ(0.toDouble(), 0.toDouble(), 0.toDouble())
            split[1] = oldPosition.add(positionToAdd).joinToString()
            return split.joinToString(":")
        }

        /**
         * Parses the [string] as [XYZ]
         * @return [XYZ] if it can be parsed, null otherwise
         */
        fun parseAsXYZ(string: String): XYZ? {
            val split = string
                .split(",")
                .map { it.replace(" ","") }
                .filter {
                    it.toDoubleOrNull() != null
                }
            return if (split.size != 3) null
            else XYZ(
                x = split[0].toDouble(),
                y = split[1].toDouble(),
                z = split[2].toDouble()
            )
        }
    }
}