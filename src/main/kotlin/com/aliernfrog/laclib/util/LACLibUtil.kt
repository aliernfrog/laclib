package com.aliernfrog.laclib.util

import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOldObject

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
    }
}