package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.os.Handler
import android.os.Looper
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter

@UnstableApi
class AdaptiveQualityManager(
    private val bandwidthMeter: DefaultBandwidthMeter,
    private val trackSelector: DefaultTrackSelector
) : BandwidthMeter.EventListener {

    private var currentNetworkType = C.NETWORK_TYPE_UNKNOWN

    init {
        // Correct listener registration with proper types
        bandwidthMeter.addEventListener(
            Handler(Looper.getMainLooper()),
            this  // Now implementing BandwidthMeter.EventListener directly
        )
    }

    override fun onBandwidthSample(
        elapsedMs: Int,
        bytesTransferred: Long,
        bitrateEstimate: Long
    ) {
        adjustQuality(bitrateEstimate)
    }

    private fun adjustQuality(bitrateEstimate: Long) {
        val params = trackSelector.parameters
        val newParams = params.buildUpon().apply {
            when {
                bitrateEstimate > 5_000_000 -> setMaxVideoSize(3840, 2160)
                bitrateEstimate > 2_500_000 -> setMaxVideoSize(1920, 1080)
                bitrateEstimate > 1_000_000 -> setMaxVideoSize(1280, 720)
                else -> setMaxVideoSize(854, 480)
            }
        }.build()

        if (params != newParams) {
            trackSelector.parameters = newParams
        }
    }

    fun updateNetworkType(networkType: Int) {
        if (currentNetworkType != networkType) {
            currentNetworkType = networkType
            forceQualityCheck()
        }
    }

    private fun forceQualityCheck() {
        trackSelector.parameters = trackSelector.parameters.buildUpon().build()
    }
}