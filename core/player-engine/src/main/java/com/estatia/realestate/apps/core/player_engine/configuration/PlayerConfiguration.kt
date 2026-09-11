package com.estatia.realestate.apps.core.player_engine.configuration

import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import androidx.media3.common.MediaItem
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Utility
data class PlayerConfiguration(
    val mediaItem: MediaItem,
    val mediaSourceFactory: MediaSource.Factory,
    val loadControl: LoadControl,
    val trackSelectionParameters: TrackSelectionParameters,
    val livePlaybackSpeedControl: LivePlaybackSpeedControl?,
    val hdrMode: HdrConfiguration.HdrMode = HdrConfiguration.HdrMode.None
)
