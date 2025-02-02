package com.application.real_estate_app.feature_mediaplayer.core

import androidx.media3.common.TrackGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.application.real_estate_app.core.utils.system.BatteryOptimizationManager
import com.application.real_estate_app.core.utils.system.DeviceUtils
import com.google.common.collect.ImmutableList
import java.lang.Long.min
import javax.inject.Inject

// Adaptive bitrate logic
@UnstableApi
class ABRStrategy @Inject constructor(
    private val networkUtils: Networkutils,
    private val batteryManager: BatteryOptimizationManager,
    private val deviceUtils: DeviceUtils
) : AdaptiveTrackSelection.Factory() {

    override fun createAdaptiveTrackSelection(
        trackGroup: TrackGroup,
        tracks: IntArray,
        type: Int,
        bandwidthMeter: BandwidthMeter,
        adaptationCheckpoints: ImmutableList<AdaptiveTrackSelection.AdaptationCheckpoint>
    ): AdaptiveTrackSelection {
        return object : AdaptiveTrackSelection(trackGroup, tracks, bandwidthMeter) {
            override fun selectTracks(): Pair<Int, Int> {
                // Custom logic combining network + device capabilities
                val maxBitrate = deviceUtils.getMaxSupportedBitrate()
                val adjustedBitrate = min(maxBitrate, networkUtils.estimatedThroughputbps)

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
            networkUtils.isUnmeteredConnection() -> baseBitrate
            else -> (baseBitrate * 0.8).toLong()
        }
    }
}