package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

@UnstableApi
class ContentPreloader(
    private val context: Context,
    private val cache: Cache,
    private val networkTypeFlow: Flow<Int>
) {
    private val preloadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val activePreloads = mutableMapOf<String, Job>()

    // Initialize CacheDataSource factory
    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun smartPreload(mediaItems: List<MediaItem>, currentPosition: Int) {
        preloadScope.launch {
            val networkType = networkTypeFlow.first()
            val preloadCount = when (networkType) {
                C.NETWORK_TYPE_WIFI -> 5
                C.NETWORK_TYPE_4G -> 3
                else -> 1
            }

            val itemsToPreload = mediaItems.subList(
                currentPosition + 1,
                (currentPosition + preloadCount).coerceAtMost(mediaItems.lastIndex)
            )

            itemsToPreload.forEach { item ->
                activePreloads[item.mediaId]?.cancel()
                activePreloads[item.mediaId] = preloadItem(item)
            }
        }
    }

    private fun preloadItem(mediaItem: MediaItem): Job {
        return preloadScope.launch {
            try {
                val uri = Uri.parse(mediaItem.mediaMetadata.displayTitle?.toString() ?: "")
                val dataSpec = DataSpec(uri)

                CacheWriter(
                    cacheDataSourceFactory.createDataSource(),
                    dataSpec,
                    null
                )  // cacheKey
                { requestLength, bytesCached, newBytesCached ->
                    // Optional: Track progress if needed
                }.cache()

                sendAnalytics(AnalyticsEvent.PreloadSuccess(mediaItem.mediaId))
            } catch (e: IOException) {
                handlePreloadError(e, mediaItem.mediaId)
            }
        }
    }

    private fun handlePreloadError(error: IOException, mediaId: String) {
        // Implement error handling logic
        error.printStackTrace()
        sendAnalytics(AnalyticsEvent.PreloadFailure(mediaId, error.message))
    }

    private fun sendAnalytics(event: AnalyticsEvent) {
        // Implement your analytics tracking
        when (event) {
            is AnalyticsEvent.PreloadSuccess -> {
                // Track successful preload
            }
            is AnalyticsEvent.PreloadFailure -> {
                // Track failed preload
            }
        }
    }

    sealed class AnalyticsEvent {
        data class PreloadSuccess(val mediaId: String) : AnalyticsEvent()
        data class PreloadFailure(val mediaId: String, val error: String?) : AnalyticsEvent()
    }
}