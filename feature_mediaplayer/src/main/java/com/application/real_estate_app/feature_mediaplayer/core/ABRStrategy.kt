package com.application.real_estate_app.feature_mediaplayer.core

import androidx.media3.common.TrackGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.chunk.MediaChunk
import androidx.media3.exoplayer.source.chunk.MediaChunkIterator
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.application.real_estate_app.core.domain.interfaces.IBatteryManager
import com.application.real_estate_app.core.domain.interfaces.IDeviceUtils
import com.application.real_estate_app.core.domain.interfaces.INetworkUtils
import com.google.common.collect.ImmutableList
import java.lang.Long.min
import javax.inject.Inject

// Adaptive bitrate logic
@UnstableApi
class ABRStrategy @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val deviceUtils: IDeviceUtils
) : AdaptiveTrackSelection.Factory() {

    override fun createAdaptiveTrackSelection(
        trackGroup: TrackGroup,
        tracks: IntArray,
        type: Int,
        bandwidthMeter: BandwidthMeter,
        adaptationCheckpoints: ImmutableList<AdaptiveTrackSelection.AdaptationCheckpoint>
    ): AdaptiveTrackSelection {
        return object : AdaptiveTrackSelection(trackGroup, tracks, bandwidthMeter) {

            override fun updateSelectedTrack(
                nowMs: Long,
                bufferedDurationUs: Long,
                availableDurationUs: Long,
                queue: MutableList<out MediaChunk>,
                mediaChunkIterators: Array<out MediaChunkIterator>
            ) {
                // Custom logic: Adjust bitrate based on network + device capabilities
                val maxBitrate = deviceUtils.getMaxSupportedBitrate()
                val adjustedBitrate = min(maxBitrate, networkUtils.estimatedThroughputbps())

                // Apply max allowed bitrate based on battery status
                val bitrateCap = adjustMaxBitrate(adjustedBitrate)

                // Call base class implementation with adjusted parameters
                super.updateSelectedTrack(
                    nowMs,
                    bufferedDurationUs,
                    availableDurationUs,
                    queue,
                    mediaChunkIterators
                )
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
            !networkUtils.isNetworkMetered() -> baseBitrate
            else -> (baseBitrate * 0.8).toLong()
        }
    }
}