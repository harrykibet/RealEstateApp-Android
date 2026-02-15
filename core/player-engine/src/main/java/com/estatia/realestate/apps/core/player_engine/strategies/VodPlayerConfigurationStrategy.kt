package com.estatia.realestate.apps.core.player_engine.strategies

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

@UnstableApi
class VodPlayerConfigurationStrategy @Inject constructor()
    : PlayerConfigurationStrategy {

    override fun configure(
        context: Context,
        builder: ExoPlayer.Builder
    ): ExoPlayer.Builder {

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                3000,  // larger buffer for stability
                6000,
                1500,
                2000
            )
            .build()

        return builder
            .setLoadControl(loadControl)
    }

    override fun createMediaItem(url: String): MediaItem {
        return MediaItem.fromUri(url)
    }

    override fun isLive(): Boolean = false
}
