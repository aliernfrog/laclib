package com.aliernfrog.laclib.data

data class LACMapObjectFilter(
    val query: String = "",
    val caseSensitive: Boolean = true,
    val exactMatch: Boolean = true,
    val filterName: String? = null
)
