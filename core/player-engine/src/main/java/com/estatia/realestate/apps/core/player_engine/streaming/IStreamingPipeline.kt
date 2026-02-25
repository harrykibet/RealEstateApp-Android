package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.source.MediaSource
import com.estatia.realestate.apps.core.domain.interfaces.MediaType

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
     */
    fun createMediaItem(uri: Uri, mediaType: MediaType): MediaItem

    /**
     * Asynchronous prefetch entry point.
     * Feature layer may call this.
     */
    fun prefetch(uri: Uri)

    /**
     * Observability.
     */
    suspend fun cacheSizeBytes(): Long

    suspend fun isFullyCached(cacheKey: String): Boolean
}