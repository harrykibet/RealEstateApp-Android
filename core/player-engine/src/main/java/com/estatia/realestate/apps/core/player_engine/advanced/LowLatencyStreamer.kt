package com.estatia.realestate.apps.core.player_engine.advanced

import androidx.media3.common.MediaItem
import javax.inject.Inject

/**
 * Provides utilities for configuring low-latency live playback.
 *
 * This class is responsible for creating a [MediaItem] configured
 * with a [MediaItem.LiveConfiguration] tuned for reduced live delay.
 *
 * It does NOT mutate or control the ExoPlayer instance directly.
 * Instead, it produces properly configured media items that can be
 * applied by the player manager.
 *
 *
 * if your feed contains both:
 *   - Live listings
 *   - Pre-recorded property videos
 * You should apply low latency only when the content is live.
 *
 * Low-latency configuration details:
 *
 * - targetOffsetMs = 1000ms
 *      Attempts to keep playback ~1 second behind the live edge.
 *
 * - minPlaybackSpeed = 0.97f
 * - maxPlaybackSpeed = 1.03f
 *      Allows small playback speed adjustments to maintain live edge
 *      without noticeable pitch or tempo distortion.
 *
 * Intended usage:
 *
 * val mediaItem = lowLatencyStreamer.createLowLatencyMediaItem(url)
 * exoPlayer.setMediaItem(mediaItem)
 * exoPlayer.prepare()
 *
 * Notes:
 * - Effective low-latency playback also depends on:
 *      • LoadControl configuration
 *      • LivePlaybackSpeedControl
 *      • Stream type (LL-HLS / LL-DASH)
 *      • CDN/server support
 *
 * - These settings are primarily meaningful for live streams.
 *   For VOD content, LiveConfiguration has no effect.
 */
class LowLatencyStreamer @Inject constructor() {

    /**
     * Creates a [MediaItem] configured for low-latency live playback.
     *
     * @param uri The live stream URL (LL-HLS / LL-DASH).
     * @return A [MediaItem] with low-latency live configuration applied.
     */
    fun createLowLatencyMediaItem(uri: String): MediaItem {

        val liveConfig = MediaItem.LiveConfiguration.Builder()
            .setTargetOffsetMs(1000L)
            .setMinPlaybackSpeed(0.97f)
            .setMaxPlaybackSpeed(1.03f)
            .build()

        return MediaItem.Builder()
            .setUri(uri)
            .setLiveConfiguration(liveConfig)
            .build()
    }
}

