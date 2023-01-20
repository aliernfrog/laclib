package com.aliernfrog.laclib.data

data class LACMapObjectFilter(
    val filterName: String,
    val query: String = "",
    val caseSensitive: Boolean = true,
    val exactMatch: Boolean = true
)
