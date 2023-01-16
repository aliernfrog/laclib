package com.aliernfrog.laclib.data

import com.aliernfrog.laclib.enum.LACMapOldObject

data class LACMapObject(
    val line: String,
    val lineNumber: Int,
    val canReplaceWith: LACMapOldObject? = null
)
