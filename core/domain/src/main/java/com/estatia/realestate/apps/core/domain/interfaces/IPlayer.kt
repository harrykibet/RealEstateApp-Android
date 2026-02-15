package com.estatia.realestate.apps.core.domain.interfaces

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface IPlayer {
    fun releasePlayer(mediaId: String)
    fun preloadMedia(mediaId: String)
    fun detachPlayer()
    fun resume()
    fun pause()
    fun getCurrentPlayer(): ExoPlayer?
    fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    )

    fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer
}