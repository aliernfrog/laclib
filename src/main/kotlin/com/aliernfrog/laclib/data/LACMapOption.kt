package com.aliernfrog.laclib.data

import com.aliernfrog.laclib.enum.LACMapOptionType

data class LACMapOption(
    val type: LACMapOptionType,
    val label: String,
    val value: String,
    val line: Int
)
