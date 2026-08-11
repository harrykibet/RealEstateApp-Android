package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adaptive policy that scales the cache ceiling based on available device storage.
 */
@Singleton
class AdaptiveCacheSizingPolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils
) : ICacheSizingPolicy {

    companion object {
        private const val DEFAULT_CACHE_BYTES = 512L * 1024 * 1024 // 512MB
        private const val MIN_CACHE_BYTES = 128L * 1024 * 1024 // 128MB
        private const val STORAGE_BUDGET_PERCENT = 0.10 // Use up to 10% of available space
    }

    override fun calculateCacheSizeBytes(): Long {
        val availableMB = deviceUtils.getAvailableStorageMB()
        
        // If storage query failed, stick to default
        if (availableMB < 0) return DEFAULT_CACHE_BYTES

        val availableBytes = availableMB * 1024 * 1024
        val budgetBytes = (availableBytes * STORAGE_BUDGET_PERCENT).toLong()

        // Clamp between 128MB and 512MB
        return budgetBytes.coerceIn(MIN_CACHE_BYTES, DEFAULT_CACHE_BYTES)
    }
}
