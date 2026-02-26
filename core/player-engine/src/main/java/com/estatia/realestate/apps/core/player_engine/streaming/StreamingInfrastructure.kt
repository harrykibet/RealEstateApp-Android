package com.estatia.realestate.apps.core.player_engine.streaming

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import java.io.File
import java.util.concurrent.Executors

@UnstableApi
class StreamingInfrastructure(
    context: Context,
    playbackCacheSizeBytes: Long = 512L * 1024 * 1024
) {

    private val databaseProvider = StandaloneDatabaseProvider(context)

    // ----------------------------
    // Playback Cache (LRU)
    // ----------------------------

    val playbackCache = SimpleCache(
        File(context.cacheDir, "playback_cache"),
        LeastRecentlyUsedCacheEvictor(playbackCacheSizeBytes),
        databaseProvider
    )

    // ----------------------------
    // Offline Cache (Durable)
    // ----------------------------

    val offlineCache = SimpleCache(
        File(context.filesDir, "offline_cache"),
        NoOpCacheEvictor(),
        databaseProvider
    )

    // ----------------------------
    // Network
    // ----------------------------

    val upstreamFactory: DataSource.Factory =
        DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

    // ----------------------------
    // Playback DataSource (offline → playback → network)
    // ----------------------------

    private val offlineLayer =
        CacheDataSource.Factory()
            .setCache(offlineCache)
            .setUpstreamDataSourceFactory(upstreamFactory)

    val playbackDataSourceFactory: DataSource.Factory =
        CacheDataSource.Factory()
            .setCache(playbackCache)
            .setUpstreamDataSourceFactory(offlineLayer)

    val mediaSourceFactory: MediaSource.Factory =
        DefaultMediaSourceFactory(playbackDataSourceFactory)

    // ----------------------------
    // DownloadManager (offline only)
    // ----------------------------

    private val downloadExecutor =
        Executors.newFixedThreadPool(2)

    val downloadManager = DownloadManager(
        context,
        databaseProvider,
        offlineCache,
        upstreamFactory,
        downloadExecutor
    )

    fun release() {
        downloadManager.release()
        downloadExecutor.shutdown()
        playbackCache.release()
        offlineCache.release()
    }
}