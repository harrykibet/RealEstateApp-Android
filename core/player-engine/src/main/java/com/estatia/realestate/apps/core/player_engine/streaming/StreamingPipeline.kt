package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
class StreamingPipeline @Inject constructor(
    private val feedPrefetchController: FeedPrefetchController,
    private val mediaSourceFactory: MediaSource.Factory,
    private val offlineDownloadController: OfflineDownloadController
) : IStreamingPipeline {


    override fun mediaSourceFactory(): MediaSource.Factory {
        return mediaSourceFactory
    }

    override fun createMediaItem(
        uri: Uri,
        mediaType: MediaType
    ): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
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

    override fun prefetch(uri: Uri, priority: PrefetchPriority) =
        feedPrefetchController.prefetch(uri, priority)

    fun downloadOffline(mediaId: String, uri: Uri) =
        offlineDownloadController.download(mediaId, uri)

    fun removeOffline(mediaId: String) =
        offlineDownloadController.remove(mediaId)
}