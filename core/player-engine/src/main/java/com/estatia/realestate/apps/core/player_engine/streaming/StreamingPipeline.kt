package com.estatia.realestate.apps.core.player_engine.streaming

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.di.StreamingDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
class StreamingPipeline @Inject constructor(
    @ApplicationContext context: Context,
    @StreamingDispatcher dispatcher: CoroutineDispatcher
) : IStreamingPipeline {

    private val infrastructure = StreamingInfrastructure(context)

    private val feedPrefetchController =
        FeedPrefetchController(
            infrastructure.playbackDataSourceFactory,
            dispatcher
        )

    private val offlineController =
        OfflineDownloadController(
            infrastructure.downloadManager
        )

    override fun mediaSourceFactory(): MediaSource.Factory =
        infrastructure.mediaSourceFactory

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

    override fun prefetch(uri: Uri) =
        feedPrefetchController.prefetch(uri)

    fun downloadOffline(mediaId: String, uri: Uri) =
        offlineController.download(mediaId, uri)

    fun removeOffline(mediaId: String) =
        offlineController.remove(mediaId)
}