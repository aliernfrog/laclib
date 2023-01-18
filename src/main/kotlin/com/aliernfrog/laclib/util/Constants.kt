package com.aliernfrog.laclib.util

import com.aliernfrog.laclib.data.LACMapObjectFilter

val ILLEGAL_ROLE_CHARS = listOf(",",":")

val DEFAULT_MAP_OBJECT_FILTERS = listOf(
    LACMapObjectFilter(
        filterName =  "Trigger box",
        query = "Trigger_Box_Editor",
        caseSensitive = true,
        exactMatch = true
    ),
    LACMapObjectFilter(
        filterName = "Team-deathmatch spawnpoint",
        query = "Team_",
        caseSensitive = true,
        exactMatch = false
    ),
    LACMapObjectFilter(
        filterName = "Racing checkpoint",
        query = "Checkpoint_Editor",
        caseSensitive = true,
        exactMatch = false
    )
)