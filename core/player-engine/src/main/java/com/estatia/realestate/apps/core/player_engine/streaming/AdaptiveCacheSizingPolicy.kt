package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adaptive policy that scales the cache ceiling based on available device storage.
 */
@Singleton
class AdaptiveCacheSizingPolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils,
    private val config: IPlayerTuningConfig
) : ICacheSizingPolicy {

    override fun calculateCacheSizeBytes(): Long {
        val tuning = config.playerTuning
        val availableMB = deviceUtils.getAvailableStorageMB()
        
        // If storage query failed, stick to default
        if (availableMB < 0) return tuning.defaultCacheBytes

        val availableBytes = availableMB * 1024 * 1024
        val budgetBytes = (availableBytes * tuning.storageBudgetPercent).toLong()

        // Clamp between min and default
        return budgetBytes.coerceIn(tuning.minCacheBytes, tuning.defaultCacheBytes)
    }
}
