package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * Container for a pooled player instance and its associated state.
 */
// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Helper
data class ManagedPlayer(
    val mediaId: String,
    val mediaType: MediaType,
    val player: ExoPlayer,
    var analyticsListener: PlaybackAnalyticsListener,
    val reducer: PlaybackStateReducer
)
