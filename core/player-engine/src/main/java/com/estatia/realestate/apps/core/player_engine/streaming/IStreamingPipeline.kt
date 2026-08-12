package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.model.property.MediaType

/**
 * Abstraction over the entire streaming stack.
 *
 * Owns:
 * - Cache
 * - DataSource
 * - MediaSourceFactory creation
 * - Prefetch orchestration
 *
 * Upper layers must not depend on Media3 cache internals.
 */
interface IStreamingPipeline {

    /**
     * Provides a MediaSource.Factory configured with:
     * - Cache
     * - Network stack
     * - Future protocol logic (HLS/DASH/etc)
     */
    fun mediaSourceFactory(): MediaSource.Factory

    /**
     * Create a MediaItem in a streaming-aware way.
     * (Allows future token injection, signed URLs, DRM, etc.)
     *
     * @param mediaId Stable ID used as custom cache key to unify playback and offline caches.
     * @param uri The source URI for the media.
     * @param mediaType The type of media.
     * @param title Optional title for media session metadata.
     * @param artist Optional artist name for media session metadata.
     */
    fun createMediaItem(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        title: String? = null,
        artist: String? = null,
        qualityHint: String? = null
    ): MediaItem

    /**
     * Asynchronous prefetch entry point.
     * Feature layer may call this to warm the cache for upcoming media.
     * 
     * @param mediaId Stable ID used as custom cache key.
     * @param uri The URI to prefetch.
     * @param priority The priority of the prefetch request.
     */
    fun warm(mediaId: String, uri: Uri, priority: WarmPriority)

    /**
     * Notifies the pipeline that buffering has started, allowing it to adjust prefetch logic.
     */
    fun onBufferingStarted()

    /**
     * Notifies the pipeline that buffering has ended.
     */
    fun onBufferingEnded()
}
