package com.application.real_estate_app.feature_mediaplayer.drm

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject
import javax.inject.Singleton

// Secure decryption
@Singleton
@UnstableApi
class ContentDecryptor @Inject constructor(
    private val widevineManager: WidevineManager
) {
    fun prepareDrmSession(player: ExoPlayer, licenseUrl: String) {
        player.drmSessionManager = widevineManager.buildDrmSessionManager(licenseUrl)
        player.setHandleAudioBecomingNoisy(true)
    }
}