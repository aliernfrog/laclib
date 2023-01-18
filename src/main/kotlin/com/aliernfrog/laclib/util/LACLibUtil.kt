package com.aliernfrog.laclib.util

import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.enum.LACMapOldObject

class LACLibUtil {
    companion object {
        fun getEditorLineType(line: String): LACMapLineType {
            return LACMapLineType.values().filter { !it.ignoreWhenFiltering }
                .find { it.matches(line) } ?: LACMapLineType.UNKNOWN
        }

        fun findReplacementForObject(line: String): LACMapOldObject? {
            val objectName = line.split(":")[0]
            return LACMapOldObject.values().find { it.objectName == objectName }
        }
    }
}