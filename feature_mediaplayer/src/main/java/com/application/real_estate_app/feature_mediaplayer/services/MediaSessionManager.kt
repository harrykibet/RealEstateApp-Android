package com.application.real_estate_app.feature_mediaplayer.services

import android.content.Context
import android.media.MediaRouter
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

// Cast/WearOS handoff
@Singleton
@UnstableApi
class MediaSessionManager @Inject constructor(
    context: Context,
    private val player: ExoPlayer
) {
    private val mediaSession = MediaSession.Builder(context, player).build()

    init {
        mediaSession.setCallback(object : MediaSession.Callback {
            override fun onPlay() {
                player.play()
            }
        })
    }

    fun connectToChromecast() {
        val mediaRouter = MediaRouter.getInstance(context)
        mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_AUDIO, object : MediaRouter.Callback() {})
    }
}