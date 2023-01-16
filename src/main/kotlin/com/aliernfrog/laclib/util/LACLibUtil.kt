package com.aliernfrog.laclib.util

import com.aliernfrog.laclib.data.LACMapObjectFilter
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

        fun lineMatchesObjectFilter(line: String, filter: LACMapObjectFilter): Boolean {
            val type = getEditorLineType(line)
            if (type != LACMapLineType.OBJECT) return false
            val objectName = type.getValue(line)
            val filterQuery = filter.query
            val ignoreCase = !filter.caseSensitive
            if (filterQuery.isBlank()) return false
            return if (filter.exactMatch) {
                objectName.equals(filterQuery, ignoreCase)
            } else {
                objectName.startsWith(filterQuery, ignoreCase)
            }
        }
    }
}