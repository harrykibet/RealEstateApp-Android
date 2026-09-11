package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.architecture.annotations.PlayerState
import com.estatia.realestate.apps.core.architecture.annotations.AllowedArchitectureDependency

/**
 * A wrapper around [ExoPlayer] that links it to a dedicated [PlaybackStateReducer].
 */
// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@PlayerState
data class ManagedPlayer(
    val mediaId: String,
    val mediaType: MediaType,
    val player: ExoPlayer,
    var analyticsListener: PlaybackAnalyticsListener,
    @AllowedArchitectureDependency("Engine state owner")
    val reducer: PlaybackStateReducer
)
