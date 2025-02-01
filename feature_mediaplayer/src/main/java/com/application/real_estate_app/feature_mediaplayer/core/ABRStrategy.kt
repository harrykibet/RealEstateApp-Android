package com.application.real_estate_app.feature_mediaplayer.core

import androidx.media3.common.TrackGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.application.real_estate_app.core.utils.system.BatteryOptimizationManager
import com.application.real_estate_app.core.utils.system.DeviceUtils
import java.lang.Long.min
import javax.inject.Inject

// Adaptive bitrate logic
@UnstableApi
class ABRStrategy @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val deviceCapabilityChecker: DeviceCapabilityChecker,
    private val batteryManager: BatteryOptimizationManager,
    private val deviceUtils: DeviceUtils
) : AdaptiveTrackSelection.Factory() {

    override fun createAdaptiveTrackSelection(
        trackGroup: TrackGroup,
        bandwidthMeter: BandwidthMeter,
        adaptiveSettings: AdaptiveTrackSelection.AdaptiveSettings
    ): AdaptiveTrackSelection {
        return object : AdaptiveTrackSelection(trackGroup, bandwidthMeter, adaptiveSettings) {
            override fun selectTracks(): Pair<Int, Int> {
                // Custom logic combining network + device capabilities
                val maxBitrate = deviceCapabilityChecker.getMaxSupportedBitrate()
                val adjustedBitrate = min(maxBitrate, networkMonitor.estimatedThroughputbps)

                return super.selectTracks().copy(adjustedBitrate)
            }
        }
    }

    fun adjustMaxBitrate(defaultMax: Long): Long {
        return if (batteryManager.shouldThrottlePerformance()) {
            (defaultMax * 0.6).toLong()
        } else {
            defaultMax
        }
    }

    fun getMaxAllowedBitrate(): Long {
        val baseBitrate = deviceUtils.getMaxSupportedBitrate()
        return when {
            deviceUtils.isLowRamDevice() -> (baseBitrate * 0.6).toLong()
            networkMonitor.isUnmeteredConnection() -> baseBitrate
            else -> (baseBitrate * 0.8).toLong()
        }
    }
}