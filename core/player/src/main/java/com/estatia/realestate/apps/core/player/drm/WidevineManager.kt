package com.estatia.realestate.apps.core.player.drm

import java.util.UUID
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

// DRM license handling
@Singleton
@UnstableApi
class WidevineManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    val drmSchemeUuid: UUID get() = C.WIDEVINE_UUID

    fun createHttpMediaDrmCallback(licenseUrl: String): HttpMediaDrmCallback {
        return HttpMediaDrmCallback(licenseUrl, DefaultHttpDataSource.Factory())
    }

    fun buildDrmSessionManager(licenseUrl: String): DrmSessionManager {
        // Create OkHttpDataSource.Factory using OkHttpClient
        val dataSourceFactory = OkHttpDataSource.Factory(okHttpClient)

        // Create HttpMediaDrmCallback with the DataSource.Factory
        val drmCallback = HttpMediaDrmCallback(licenseUrl, dataSourceFactory)

        // Build the DrmSessionManager
        return DefaultDrmSessionManager.Builder()
            .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
            .build(drmCallback)
    }
}
