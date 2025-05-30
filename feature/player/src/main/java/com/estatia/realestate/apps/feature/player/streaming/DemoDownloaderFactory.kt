package com.estatia.realestate.apps.feature.player.streaming

import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.Downloader
import androidx.media3.exoplayer.offline.DownloaderFactory
import androidx.media3.exoplayer.offline.ProgressiveDownloader

@UnstableApi
class DemoDownloaderFactory(
    private val cacheDataSourceFactory: CacheDataSource.Factory
) : DownloaderFactory {
    override fun createDownloader(request: DownloadRequest): Downloader {
        val mediaItem = MediaItem.fromUri(request.uri) // ✅ Convert DownloadRequest to MediaItem

        return ProgressiveDownloader(
            mediaItem,  // ✅ Correct type
            cacheDataSourceFactory // ✅ Use CacheDataSource.Factory directly
        )
    }
}
