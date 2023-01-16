package com.aliernfrog.laclib.data

data class LACMapObjectFilter(
    var query: String = "",
    var caseSensitive: Boolean = true,
    var exactMatch: Boolean = true,
    val labelStringId: Int? = null
)
