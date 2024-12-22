package com.aliernfrog.laclib.enum

/**
 * Contains LAC map objects which no longer exist and can be replaced.
 * @property objectName Name of the object
 * @property replaceName Name of the object the object can be replaced with
 * @property replaceScale Scale to use when replacing the object
 * @property replaceColor Color to use when replacing the object
 */
enum class LACMapOldObject(
    val objectName: String,
    val replaceName: String,
    val replaceScale: String? = null,
    val replaceColor: String? = null
) {
    BLOCK_1BY1(
        objectName = "Block_1by1_Editor",
        replaceName = "Block_Scalable_Editor",
        replaceScale = "1.0,1.0,1.0"
    ),

    BLOCK_3BY6(
        objectName = "Block_3by6_Editor",
        replaceName = "Block_Scalable_Editor",
        replaceScale = "3.0,6.0,1.0"
    ),

    RED_SOFA(
        objectName = "Sofa_Chunk_Red_Editor",
        replaceName = "Sofa_Chunk_Editor",
        replaceColor = "color{1.00,0.00,0.00}"
    )
}