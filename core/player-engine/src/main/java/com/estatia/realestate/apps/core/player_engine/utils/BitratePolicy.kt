package com.estatia.realestate.apps.core.player_engine.utils

import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import javax.inject.Inject

class DynamicBitratePolicy @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val deviceUtils: IDeviceUtils
) {

    fun calculateMaxVideoBitrate(mediaType: MediaType): Int {

        val deviceCap = deviceUtils.getMaxSupportedBitrate()
        val networkCap = networkUtils.estimatedThroughputbps()

        val base = minOf(deviceCap, networkCap)

        val environmentAdjusted = when {
            batteryManager.shouldThrottlePerformance() -> base * 0.6
            deviceUtils.isLowRamDevice() -> base * 0.7
            networkUtils.isNetworkMetered() -> base * 0.8
            else -> base
        }

        return when (mediaType) {
            MediaType.LIVE -> (environmentAdjusted * 0.85).toInt() // safer for rebuffer
            MediaType.VOD -> environmentAdjusted.toInt() // more aggressive quality
        }
    }
}
