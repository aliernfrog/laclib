package com.aliernfrog.laclib.data

data class LACMapToMerge(
    val mapName: String,
    val content: String,
    var mergePosition: String = "0,0,0",
    var mergeSpawnpoints: Boolean = true,
    var mergeRacingCheckpoints: Boolean = false,
    var mergeTDMSpawnpoints: Boolean = false
)
