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

        val deviceCap = deviceUtils.getMaxSupportedBitrate() // probably Int
        val networkCap = networkUtils.estimatedThroughputbps() // Long

        val base = minOf(deviceCap, networkCap)

        val baseDouble = base.toDouble()

        val environmentAdjusted = when {
            batteryManager.shouldThrottlePerformance() -> baseDouble * 0.6
            deviceUtils.isLowRamDevice() -> baseDouble * 0.7
            networkUtils.isNetworkMetered() -> baseDouble * 0.8
            else -> baseDouble
        }

        return when (mediaType) {
            MediaType.LIVE -> (environmentAdjusted * 0.85).toInt()
            MediaType.VOD -> environmentAdjusted.toInt()
        }
    }
}
