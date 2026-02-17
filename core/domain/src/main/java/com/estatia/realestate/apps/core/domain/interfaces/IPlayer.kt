package com.estatia.realestate.apps.core.domain.interfaces

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface IPlayer {
    suspend fun releasePlayer(mediaId: String)
    suspend fun getCurrentPlayer(): ExoPlayer?
    suspend fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    )

    suspend fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer
    suspend fun preloadMedia(mediaId: String)
    suspend fun pause()
    suspend fun resume()
    suspend fun detachPlayer()
}