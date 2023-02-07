package com.aliernfrog.laclib.data

data class LACMapDownloadableMaterial(

    /**
     * URL of the downloadable material.
     */
    val url: String,

    /**
     * Name of the downloadable material.
     */
    val name: String,

    /**
     * List of [LACMapObject]s which use the material.
     */
    val usedBy: List<LACMapObject>
)
