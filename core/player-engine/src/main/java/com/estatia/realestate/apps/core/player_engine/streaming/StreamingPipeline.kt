package com.estatia.realestate.apps.core.player_engine.streaming

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.MediaType
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
internal class StreamingPipeline @Inject constructor(
    private val cacheWarmer: MediaCacheWarmer,
    private val mediaSourceFactory: MediaSource.Factory,
    private val offlineDownloadController: OfflineDownloadController,
    private val cacheKeyFactory: ICacheKeyFactory,
    private val uriResolver: StreamingUriResolver,
    private val deviceUtils: com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
) : IStreamingPipeline {


    override fun mediaSourceFactory(): MediaSource.Factory {
        return mediaSourceFactory
    }

    override fun createMediaItem(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        title: String?,
        artist: String?,
        qualityHint: String?
    ): MediaItem {
        val platformUri = uri.value.toUri()
        val stableKey = cacheKeyFactory.resolveStableKey(platformUri, mediaId, qualityHint)

        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .build()

        return MediaItem.Builder()
            .setUri(platformUri)
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

    override fun warm(mediaId: String, uri: MediaReference, priority: WarmPriority, qualityHint: String?) {
        val resolvedUri = uriResolver.resolve(uri.value.toUri())
        val resolvedHint = qualityHint ?: deviceUtils.getVideoQualityHint()
        cacheWarmer.prefetch(mediaId, resolvedUri, priority, resolvedHint)
    }

    override fun onBufferingStarted() =
        cacheWarmer.onBufferingStarted()

    override fun onBufferingEnded() =
        cacheWarmer.onBufferingEnded()

    fun downloadOffline(mediaId: String, uri: MediaReference) =
        offlineDownloadController.download(mediaId, uri.value.toUri())

    fun removeOffline(mediaId: String) =
        offlineDownloadController.remove(mediaId)
}
