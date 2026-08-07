package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
class StreamingPipeline @Inject constructor(
    private val cacheWarmer: MediaCacheWarmer,
    private val mediaSourceFactory: MediaSource.Factory,
    private val offlineDownloadController: OfflineDownloadController
) : IStreamingPipeline {


    override fun mediaSourceFactory(): MediaSource.Factory {
        return mediaSourceFactory
    }

    override fun createMediaItem(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType
    ): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setCustomCacheKey(mediaId) // Stable key for caching unified with offline
            .apply {
                if (mediaType == MediaType.LIVE) {
                    setLiveConfiguration(
                        MediaItem.LiveConfiguration.Builder()
                            .setTargetOffsetMs(1000L)
                            .setMinPlaybackSpeed(0.97f)
                            .setMaxPlaybackSpeed(1.03f)
                            .build()
                    )
                }
            }
            .build()
    }

    override fun warm(uri: Uri, priority: WarmPriority) =
        cacheWarmer.prefetch(uri, priority)

    override fun onBufferingStarted() =
        cacheWarmer.onBufferingStarted()

    override fun onBufferingEnded() =
        cacheWarmer.onBufferingEnded()

    fun downloadOffline(mediaId: String, uri: Uri) =
        offlineDownloadController.download(mediaId, uri)

    fun removeOffline(mediaId: String) =
        offlineDownloadController.remove(mediaId)
}
