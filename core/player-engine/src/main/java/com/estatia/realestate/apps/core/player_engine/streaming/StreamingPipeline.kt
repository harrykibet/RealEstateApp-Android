package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
internal class StreamingPipeline @Inject constructor(
    private val cacheWarmer: MediaCacheWarmer,
    private val mediaSourceFactory: MediaSource.Factory,
    private val offlineDownloadController: OfflineDownloadController,
    private val cacheKeyFactory: ICacheKeyFactory
) : IStreamingPipeline {


    override fun mediaSourceFactory(): MediaSource.Factory {
        return mediaSourceFactory
    }

    override fun createMediaItem(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        title: String?,
        artist: String?
    ): MediaItem {
        val stableKey = cacheKeyFactory.resolveStableKey(uri, mediaId)

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .build()

        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId(stableKey)
            .setCustomCacheKey(stableKey) // Stable key for caching unified with offline
            .setMediaMetadata(metadata)
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

    override fun warm(mediaId: String, uri: Uri, priority: WarmPriority) =
        cacheWarmer.prefetch(mediaId, uri, priority)

    override fun onBufferingStarted() =
        cacheWarmer.onBufferingStarted()

    override fun onBufferingEnded() =
        cacheWarmer.onBufferingEnded()

    fun downloadOffline(mediaId: String, uri: Uri) =
        offlineDownloadController.download(mediaId, uri)

    fun removeOffline(mediaId: String) =
        offlineDownloadController.remove(mediaId)
}
