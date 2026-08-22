package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@UnstableApi
class PlaybackConfigurationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: IPlayerTuningConfig
) : IPlaybackConfigurationProvider {

    override fun createLoadControl(mediaType: MediaType, environment: EnvironmentState): LoadControl {
        return when (mediaType) {
            MediaType.LIVE -> createLiveLoadControl(environment)
            MediaType.VOD -> createVodLoadControl(environment)
        }
    }

    override fun createPlaybackSpeedControl(mediaType: MediaType, environment: EnvironmentState): LivePlaybackSpeedControl? {
        return if (mediaType == MediaType.LIVE) {
            DefaultLivePlaybackSpeedControl.Builder()
                .setFallbackMinPlaybackSpeed(0.97f)
                .setFallbackMaxPlaybackSpeed(1.03f)
                .build()
        } else null
    }

    override fun createTrackSelectionParameters(matchScore: Float, environment: EnvironmentState): TrackSelectionParameters {
        val builder = TrackSelectionParameters.Builder(context)

        // 🧠 Match-Aware Initial Rendition:
        // High Match -> Start at 1080p if bandwidth allows.
        // Low Match -> Start at 360p to save data.
        if (matchScore > 0.9f) {
            builder.setMaxVideoSize(1920, 1080)
        } else if (matchScore < 0.4f) {
            builder.setMaxVideoSize(640, 360)
        }

        // 🌡️ Thermal Mitigation:
        // If device is hot, cap bitrate to reduce GPU/CPU load.
        if (environment.thermalStatus >= 2) { // MODERATE or higher
            builder.setMaxVideoBitrate(2_000_000) // 2Mbps cap
        }

        return builder.build()
    }

    private fun createLiveLoadControl(env: EnvironmentState): LoadControl {
        val tuning = config.playerTuning
        // ⏱️ Adaptive Buffer: Shrink targets on metered/poor connections to save user data
        val multiplier = if (env.isMetered || env.isSustainedLowBandwidth) tuning.liveBufferMultiplier else 1.0
        
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                (tuning.minBufferLiveMs * multiplier).toInt(),
                (tuning.maxBufferLiveMs * multiplier).toInt(),
                (tuning.bufferForPlaybackLiveMs * multiplier).toInt(),
                (tuning.bufferForPlaybackAfterRebufferLiveMs * multiplier).toInt()
            )
            .build()
    }

    private fun createVodLoadControl(env: EnvironmentState): LoadControl {
        val tuning = config.playerTuning
        // ⏱️ Adaptive Buffer: Shrink targets on metered/poor connections
        val multiplier = if (env.isMetered || env.isSustainedLowBandwidth) tuning.vodBufferMultiplier else 1.0

        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                (tuning.minBufferVodMs * multiplier).toInt(),
                (tuning.maxBufferVodMs * multiplier).toInt(),
                (tuning.bufferForPlaybackVodMs * multiplier).toInt(),
                (tuning.bufferForPlaybackAfterRebufferVodMs * multiplier).toInt()
            )
            .build()
    }
}
