package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject

@UnstableApi
class PlaybackConfigurationProvider @Inject constructor() : IPlaybackConfigurationProvider {

    override fun createLoadControl(mediaType: MediaType): LoadControl {
        return when (mediaType) {
            MediaType.LIVE -> createLiveLoadControl()
            MediaType.VOD -> createVodLoadControl()
        }
    }

    override fun createPlaybackSpeedControl(mediaType: MediaType)
            : LivePlaybackSpeedControl? {

        return if (mediaType == MediaType.LIVE) {
            DefaultLivePlaybackSpeedControl.Builder()
                .setFallbackMinPlaybackSpeed(0.97f)
                .setFallbackMaxPlaybackSpeed(1.03f)
                .build()
        } else null
    }

    private fun createLiveLoadControl() : LoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,
                3000,
                500,
                1000
            )
            .build()
    }

    private fun createVodLoadControl() : LoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                3000,
                6000,
                1500,
                2000
            )
            .build()
    }
}
