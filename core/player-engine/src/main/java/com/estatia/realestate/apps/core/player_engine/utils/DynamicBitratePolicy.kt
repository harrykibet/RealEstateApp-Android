package com.estatia.realestate.apps.core.player_engine.utils

import android.os.PowerManager
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject
import kotlin.math.roundToInt

class DynamicBitratePolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {

    /**
     * BOLA-lite calculation that incorporates buffer occupancy to prioritize stall prevention.
     */
    fun calculateMaxVideoBitrate(
        mediaType: MediaType,
        environment: EnvironmentState,
        bufferSeconds: Double = 5.0
    ): Int {
        val deviceCap = deviceUtils.getMaxSupportedBitrate().coerceAtLeast(1_000_000)
        val networkCap = environment.estimatedThroughputBps.coerceAtLeast(1_000_000L)

        val base = minOf(deviceCap.toDouble(), networkCap.toDouble())
        
        // 1. Environment & Device Throttling
        var adjusted = when {
            environment.shouldThrottlePerformance -> base * 0.35
            deviceUtils.isLowRamDevice() -> base * 0.55
            environment.isMetered -> base * 0.7
            else -> base
        }

        // 2. Thermal Tiers (Harden performance reduction during overheating)
        adjusted *= when {
            environment.thermalStatus >= PowerManager.THERMAL_STATUS_CRITICAL -> 0.15
            environment.thermalStatus >= PowerManager.THERMAL_STATUS_SEVERE -> 0.35
            environment.thermalStatus >= PowerManager.THERMAL_STATUS_MODERATE -> 0.7
            else -> 1.0
        }

        // 3. BOLA-lite Buffer Penalty
        // If buffer is critically low (< 2s), aggressively drop bitrate to prevent stall
        if (bufferSeconds < 2.0) {
            adjusted *= 0.5
        } else if (bufferSeconds < 5.0) {
            adjusted *= 0.8
        }

        val mediaAdjusted = when (mediaType) {
            MediaType.LIVE -> adjusted * 0.75
            MediaType.VOD -> adjusted
        }

        return mediaAdjusted.roundToInt().coerceAtLeast(500_000)
    }
}
