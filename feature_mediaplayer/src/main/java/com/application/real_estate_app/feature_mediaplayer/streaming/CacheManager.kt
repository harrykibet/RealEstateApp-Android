package com.application.real_estate_app.feature_mediaplayer.streaming

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import okhttp3.internal.cache.CacheRequest
import javax.inject.Inject
import javax.inject.Singleton

// Offline caching
@Singleton
@UnstableApi
class CacheManager @Inject constructor(
    context: Context,
    private val exoCache: Cache
) {
    private val cacheDatabase = CacheDatabase(context)

    fun prefetch(uri: String) {
        val request = CacheRequest.Builder(uri)
            .setCacheKey(uri)
            .build()

        exoCache.startReadWriteNonBlocking(uri, request)
    }

    fun getCachedSize(): Long {
        return exoCache.cacheSpace
    }
}