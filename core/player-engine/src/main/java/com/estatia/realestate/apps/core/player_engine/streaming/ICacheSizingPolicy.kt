package com.estatia.realestate.apps.core.player_engine.streaming
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Policy for determining the target size of the media cache.
 */
@Contract
interface ICacheSizingPolicy {
    /**
     * Returns the target cache size in bytes.
     */
    fun calculateCacheSizeBytes(): Long
}
