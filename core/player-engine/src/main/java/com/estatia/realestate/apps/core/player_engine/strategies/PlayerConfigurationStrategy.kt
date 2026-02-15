package com.estatia.realestate.apps.core.player_engine.strategies

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

@UnstableApi
interface PlayerConfigurationStrategy {

    fun configure(
        context: Context,
        builder: ExoPlayer.Builder
    ): ExoPlayer.Builder
    fun createMediaItem(url: String): MediaItem
    fun isLive(): Boolean
}