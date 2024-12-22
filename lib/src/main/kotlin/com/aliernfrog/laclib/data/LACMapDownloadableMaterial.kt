package com.aliernfrog.laclib.data

data class LACMapDownloadableMaterial(

    /**
     * URL of the downloadable material.
     */
    var url: String,

    /**
     * Name of the downloadable material.
     */
    var name: String,

    /**
     * List of [LACMapObject]s which use the material.
     */
    val usedBy: List<LACMapObject>,

    /**
     * Line of the material. This should only be used for sorting as it can change during editing.
     */
    internal val line: Int? = null
)
