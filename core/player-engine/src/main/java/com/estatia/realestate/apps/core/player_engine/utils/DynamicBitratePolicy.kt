package com.estatia.realestate.apps.core.player_engine.utils

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import javax.inject.Inject
class DynamicBitratePolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {

    fun calculateMaxVideoBitrate(
        mediaType: MediaType,
        environment: EnvironmentState
    ): Int {

        val deviceCap = deviceUtils.getMaxSupportedBitrate()
        val networkCap = environment.estimatedThroughputBps

        val base = minOf(deviceCap, networkCap).toDouble()

        val adjusted = when {
            environment.shouldThrottlePerformance -> base * 0.6
            deviceUtils.isLowRamDevice() -> base * 0.7
            environment.isMetered -> base * 0.8
            else -> base
        }

        val mediaAdjusted = when (mediaType) {
            MediaType.LIVE -> adjusted * 0.85
            MediaType.VOD -> adjusted
        }

        return mediaAdjusted.toInt()
    }
}