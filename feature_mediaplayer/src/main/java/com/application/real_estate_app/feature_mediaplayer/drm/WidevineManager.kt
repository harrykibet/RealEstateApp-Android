package com.application.real_estate_app.feature_mediaplayer.drm

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

// DRM license handling
@Singleton
@UnstableApi
class WidevineManager @Inject constructor(
    private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    fun buildDrmSessionManager(licenseUrl: String): DrmSessionManager {
        val drmCallback = HttpMediaDrmCallback(licenseUrl, okHttpClient)

        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)
    }
}