package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the lifecycle and state of the [MediaSession] for the playback engine.
 */
@UnstableApi
@Singleton
class MediaSessionCoordinator @Inject constructor(
    private val mediaSessionProvider: IMediaSessionProvider
) {
    private var mediaSession: MediaSession? = null

    /**
     * Updates the session to point to the specified player.
     * Creates a new session if one doesn't exist.
     */
    fun updateSession(player: ExoPlayer) {
        checkConfinement()
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
        checkConfinement()
        mediaSession?.release()
        mediaSession = null
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("MediaSessionCoordinator must only be accessed from the Main thread.")
        }
    }
}
