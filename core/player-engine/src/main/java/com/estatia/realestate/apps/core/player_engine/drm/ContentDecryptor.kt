package com.estatia.realestate.apps.core.player_engine.drm

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class ContentDecryptor @Inject constructor(
    private val widevineManager: WidevineManager
) {
    fun prepareDrmSession(player: ExoPlayer, context: Context, licenseUrl: String, mediaItem: MediaItem) {
        // 1. Create DRM session manager
        val drmSessionManager = createDrmSessionManager(licenseUrl)

        // 2. Configure media source with DRM
        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(context)
        )
            .setDrmSessionManagerProvider { drmSessionManager }
            .createMediaSource(mediaItem)

        // 3. Prepare player
        player.setMediaSource(mediaSource)
        player.prepare()
    }

    private fun createDrmSessionManager(licenseUrl: String): DefaultDrmSessionManager {
        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(
                widevineManager.drmSchemeUuid,
                FrameworkMediaDrm.DEFAULT_PROVIDER
            )
            .build(widevineManager.createHttpMediaDrmCallback(licenseUrl))
    }
}