package com.estatia.realestate.apps.core.player_engine.streaming

/**
 * Policy for determining the target size of the media cache.
 */
interface ICacheSizingPolicy {
    /**
     * Returns the target cache size in bytes.
     */
    fun calculateCacheSizeBytes(): Long
}
