package com.estatia.realestate.apps.core.player_engine.configuration

import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.MediaSource

@UnstableApi
data class PlayerConfiguration(
    val mediaItem: MediaItem,
    val mediaSourceFactory: MediaSource.Factory,
    val loadControl: LoadControl,
    val livePlaybackSpeedControl: LivePlaybackSpeedControl?,
    val hdrMode: HdrConfiguration.HdrMode = HdrConfiguration.HdrMode.None
)
