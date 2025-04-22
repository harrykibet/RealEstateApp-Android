package com.application.real_estate_app.core_interface

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

interface IExoplayer {
    fun acquirePlayer(mediaId: String): ExoPlayer
    fun releasePlayer(mediaId: String)
    fun preloadMedia(mediaId: String)
    fun attachPlayerToView(playerView: PlayerView, mediaId: String)
    fun detachPlayer()
    fun resume()
    fun pause()
}