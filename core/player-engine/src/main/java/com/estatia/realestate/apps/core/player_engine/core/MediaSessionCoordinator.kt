package com.estatia.realestate.apps.core.player_engine.core

import com.estatia.realestate.apps.core.common.concurrency.Confinement
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * Manages the lifecycle and state of the [MediaSession] for the playback engine.
 */
// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Singleton
@Helper
class MediaSessionCoordinator @Inject constructor(
    private val mediaSessionProvider: IMediaSessionProvider
) {
    private var mediaSession: MediaSession? = null

    /**
     * Updates the session to point to the specified player.
     * Creates a new session if one doesn't exist.
     */
    fun updateSession(player: ExoPlayer) {
        Confinement.checkMainThread()
        if (mediaSession == null) {
            mediaSession = mediaSessionProvider.create(player)
        } else {
            mediaSession?.player = player
        }
    }

    /**
     * Releases the active media session.
     */
    fun release() {
        Confinement.checkMainThread()
        mediaSession?.release()
        mediaSession = null
    }
}
