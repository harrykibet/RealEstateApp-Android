package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import javax.inject.Inject

@UnstableApi
class PlaybackConfigurationProvider @Inject constructor() : IPlaybackConfigurationProvider {

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

    private fun createLiveLoadControl(env: EnvironmentState): LoadControl {
        // ⏱️ Adaptive Buffer: Shrink targets on metered/poor connections to save user data
        val multiplier = if (env.isMetered || env.isSustainedLowBandwidth) 0.6 else 1.0
        
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                (800 * multiplier).toInt(),
                (2500 * multiplier).toInt(),
                (400 * multiplier).toInt(),
                (800 * multiplier).toInt()
            )
            .build()
    }

    private fun createVodLoadControl(env: EnvironmentState): LoadControl {
        // ⏱️ Adaptive Buffer: Shrink targets on metered/poor connections
        val multiplier = if (env.isMetered || env.isSustainedLowBandwidth) 0.7 else 1.0

        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                (800 * multiplier).toInt(),
                (3000 * multiplier).toInt(),
                (250 * multiplier).toInt(),
                (800 * multiplier).toInt()
            )
            .build()
    }
}
