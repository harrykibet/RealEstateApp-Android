package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Provider for creating and managing [MediaSession] instances.
 */
// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Contract
interface IMediaSessionProvider {
    /**
     * Creates a [MediaSession] for the given [player].
     */
    fun create(player: Player): MediaSession
}
