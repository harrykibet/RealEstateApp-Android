package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import javax.inject.Inject

@UnstableApi
class PlaybackConfigurationProvider @Inject constructor(
    private val configProvider: IConfigProvider
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

    private fun createLiveLoadControl(env: EnvironmentState): LoadControl {
        val tuning = configProvider.playerTuning
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
        val tuning = configProvider.playerTuning
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
