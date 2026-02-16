package com.estatia.realestate.apps.core.domain.interfaces

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface IPlayer {
    fun releasePlayer(mediaId: String)
    fun preloadMedia(mediaId: String)
    fun detachPlayer()
    suspend fun resume()
    suspend fun pause()
    fun getCurrentPlayer(): ExoPlayer?
    suspend fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    )

    suspend fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer
}