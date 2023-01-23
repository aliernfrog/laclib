package com.aliernfrog.laclib.data

import com.aliernfrog.laclib.enum.LACMapOptionType

data class LACMapOption(
    val type: LACMapOptionType,
    val label: String,
    var value: String,
    val line: Int
)
