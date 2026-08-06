package com.estatia.realestate.apps.core.player_engine.utils

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject
import kotlin.math.roundToInt

class DynamicBitratePolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {

    fun calculateMaxVideoBitrate(
        mediaType: MediaType,
        environment: EnvironmentState
    ): Int {
        val deviceCap = deviceUtils.getMaxSupportedBitrate().coerceAtLeast(1_000_000)
        val networkCap = environment.estimatedThroughputBps.coerceAtLeast(1_000_000L)

        val base = minOf(deviceCap.toDouble(), networkCap.toDouble())
        val adjusted = when {
            environment.shouldThrottlePerformance -> base * 0.35
            deviceUtils.isLowRamDevice() -> base * 0.55
            environment.isMetered -> base * 0.7
            else -> base
        }

        val mediaAdjusted = when (mediaType) {
            MediaType.LIVE -> adjusted * 0.75
            MediaType.VOD -> adjusted
        }

        return mediaAdjusted.roundToInt().coerceAtLeast(500_000)
    }
}
