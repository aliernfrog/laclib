package com.aliernfrog.laclib.util.extension

import com.aliernfrog.laclib.data.LACMapObjectFilter
import com.aliernfrog.laclib.enum.LACMapLineType
import com.aliernfrog.laclib.util.LACLibUtil

/**
 * Checks if given [line] matches filter.
 * @return true if line matches filter, false otherwise
 */
fun LACMapObjectFilter.matchesLine(line: String): Boolean {
    val type = LACLibUtil.getEditorLineType(line)
    if (type != LACMapLineType.OBJECT) return false
    val objectName = type.getValue(line)
    val filterQuery = this.query
    val ignoreCase = !this.caseSensitive
    if (filterQuery.isBlank()) return false
    return if (this.exactMatch) {
        objectName.equals(filterQuery, ignoreCase)
    } else {
        objectName.startsWith(filterQuery, ignoreCase)
    }
}