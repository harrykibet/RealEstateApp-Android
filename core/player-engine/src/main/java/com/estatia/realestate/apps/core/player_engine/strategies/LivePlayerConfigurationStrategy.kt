package com.estatia.realestate.apps.core.player_engine.strategies

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.player_engine.advanced.LowLatencyStreamer
import javax.inject.Inject

@UnstableApi
class LivePlayerConfigurationStrategy @Inject constructor(
    private val lowLatencyStreamer: LowLatencyStreamer
) : PlayerConfigurationStrategy {

    override fun configure(
        context: Context,
        builder: ExoPlayer.Builder
    ): ExoPlayer.Builder {

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,  // lower latency
                3000,
                500,
                1000
            )
            .build()

        val liveSpeedControl = DefaultLivePlaybackSpeedControl.Builder()
            .setFallbackMinPlaybackSpeed(0.97f)
            .setFallbackMaxPlaybackSpeed(1.03f)
            .build()

        return builder
            .setLoadControl(loadControl)
            .setLivePlaybackSpeedControl(liveSpeedControl)
    }

    override fun createMediaItem(url: String): MediaItem {
        // Delegate to LowLatencyStreamer
        return lowLatencyStreamer.createLowLatencyMediaItem(url)
    }

    override fun isLive(): Boolean = true
}
