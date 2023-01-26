package com.aliernfrog.laclib.map

import com.aliernfrog.laclib.data.LACMapToMerge
import com.aliernfrog.laclib.util.LACLibUtil
import com.aliernfrog.laclib.util.MAP_MERGER_MIN_REQUIRED_MAPS

/**
 * Initializes a LAC map merger instance.
 * @param mapsToMerge Initial list of [LACMapToMerge]
 */
class LACMapMerger(
    var mapsToMerge: MutableList<LACMapToMerge> = mutableListOf()
) {
    /**
     * Adds given map to merge list.
     * @param mapName name of the map
     * @param content content of the map
     */
    fun addMap(mapName: String, content: String) {
        val isAddingBaseMap = mapsToMerge.isEmpty()
        mapsToMerge.add(
            LACMapToMerge(
            mapName = mapName,
            content = content,
            mergeRacingCheckpoints = isAddingBaseMap,
            mergeTDMSpawnpoints = isAddingBaseMap
        ))
    }

    /**
     * Makes the map at given [index] base.
     */
    fun makeMapBase(index: Int) {
        val oldBase = mapsToMerge[0]
        mapsToMerge[0] = mapsToMerge[index]
        mapsToMerge[index] = oldBase
    }

    /**
     * Merges maps and returns the merged map content.
     * @param onNoEnoughMaps [Unit] to invoke when there is no enough maps to merge
     */
    fun mergeMaps(onNoEnoughMaps: () -> Unit): String {
        if (mapsToMerge.size < MAP_MERGER_MIN_REQUIRED_MAPS) {
            onNoEnoughMaps()
            return ""
        }
        val newMapLines = mutableListOf<String>()
        mapsToMerge.forEachIndexed { index, mapToMerge ->
            val isBaseMap = index == 0
            val processed = LACLibUtil.processMapToMergeContent(mapToMerge, isBaseMap)
            newMapLines.addAll(processed)
        }
        return newMapLines.joinToString("\n")
    }
}