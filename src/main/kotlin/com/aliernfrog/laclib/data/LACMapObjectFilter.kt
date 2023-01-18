package com.aliernfrog.laclib.data

data class LACMapObjectFilter(
    val filterName: String,
    var query: String = "",
    var caseSensitive: Boolean = true,
    var exactMatch: Boolean = true
)
