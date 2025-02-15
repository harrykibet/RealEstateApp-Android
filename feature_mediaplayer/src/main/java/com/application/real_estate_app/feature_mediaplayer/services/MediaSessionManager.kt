package com.application.real_estate_app.feature_mediaplayer.services

import android.content.Context
import android.media.MediaRouter
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("Unused")
@Singleton
@UnstableApi
class MediaSessionManager @Inject constructor(
    private val context: Context,
    player: ExoPlayer
) {
    private val mediaSession: MediaSession = MediaSession.Builder(context, player)
        .setCallback(object : MediaSession.Callback {})
        .build()

    fun connectToChromecast() {
        val mediaRouter = context.getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
        mediaRouter.addCallback(
            MediaRouter.ROUTE_TYPE_LIVE_AUDIO,
            object : MediaRouter.Callback() {
                override fun onRouteSelected(
                    router: MediaRouter?,
                    type: Int,
                    info: MediaRouter.RouteInfo?
                ) {
                    // Handle Chromecast selection
                }

                override fun onRouteUnselected(
                    p0: MediaRouter?,
                    p1: Int,
                    p2: MediaRouter.RouteInfo?
                ) {}

                override fun onRouteAdded(p0: MediaRouter?, p1: MediaRouter.RouteInfo?) {}

                override fun onRouteRemoved(p0: MediaRouter?, p1: MediaRouter.RouteInfo?) {}

                override fun onRouteChanged(p0: MediaRouter?, p1: MediaRouter.RouteInfo?) {}

                override fun onRouteGrouped(
                    p0: MediaRouter?,
                    p1: MediaRouter.RouteInfo?,
                    p2: MediaRouter.RouteGroup?,
                    p3: Int
                ) {}

                override fun onRouteUngrouped(
                    p0: MediaRouter?,
                    p1: MediaRouter.RouteInfo?,
                    p2: MediaRouter.RouteGroup?
                ) {}

                override fun onRouteVolumeChanged(p0: MediaRouter?, p1: MediaRouter.RouteInfo?) {}
            }
        )
    }
}
