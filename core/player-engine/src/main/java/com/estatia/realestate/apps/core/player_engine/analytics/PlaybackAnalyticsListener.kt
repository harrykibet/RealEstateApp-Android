package com.estatia.realestate.apps.core.player_engine.analytics

import android.os.SystemClock
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.domain.interfaces.IEngagementRepository
import com.estatia.realestate.apps.core.domain.interfaces.IMetricsTracker
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Listens to ExoPlayer playback state changes and reports
 * QoS (Quality of Service) analytics events such as:
 *
 * - Startup time
 * - Buffering events
 *
 * This class is ONLY responsible for analytics tracking.
 */
@UnstableApi
class PlaybackAnalyticsListener @Inject constructor(
    private val analyticsClient: IAnalyticsTracker,
    private val engagementRepository: IEngagementRepository,
    private val metricsTracker: IMetricsTracker,
    @EngineScope private val scope: CoroutineScope
) : AnalyticsListener {

    val sessionId: String = java.util.UUID.randomUUID().toString()

    @Volatile
    private var startupStartTime: Long = 0L

    @Volatile
    private var bufferingStartedAt: Long? = null

    @Volatile
    private var firstFrameSentAt: Long? = null

    @Volatile
    private var wasBackgroundedDuringBuffer: Boolean = false

    @Volatile
    private var currentMediaId: String? = null

    @Volatile
    private var totalWatchTimeMs: Long = 0L

    @Volatile
    private var lastPlayStartTime: Long? = null

    @Volatile
    private var loopCount: Int = 0

    fun markPlaybackStart(mediaId: String) {
        currentMediaId = mediaId
        startupStartTime = SystemClock.elapsedRealtime()
        bufferingStartedAt = null
        firstFrameSentAt = null
        totalWatchTimeMs = 0L
        loopCount = 0
    }

    fun release() {
        // 🏎️ Close the Loop: Ship final engagement data before the listener is recycled/discarded
        reportFinalEngagement()
    }

    private fun reportFinalEngagement() {
        val mediaId = currentMediaId ?: return
        recordWatchTime()

        val watchTime = totalWatchTimeMs
        val loops = loopCount

        scope.launch {
            // 🏎️ Authoritative Engagement Signal:
            // Ship to the domain repository rather than directly to low-level analytics.
            engagementRepository.reportMediaWatch(
                mediaId = mediaId,
                watchTimeMs = watchTime,
                loopCount = loops
            )
        }
    }

    fun onAppBackgrounded() {
        if (bufferingStartedAt != null) {
            wasBackgroundedDuringBuffer = true
        }
        recordWatchTime()
    }

    private fun recordWatchTime() {
        val start = lastPlayStartTime ?: return
        val sessionWatchTime = SystemClock.elapsedRealtime() - start
        if (sessionWatchTime > 0) {
            totalWatchTimeMs += sessionWatchTime
            metricsTracker.trackDuration("player.watch.duration", sessionWatchTime.milliseconds)
            lastPlayStartTime = SystemClock.elapsedRealtime()
        }
    }

    override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
        if (isPlaying) {
            lastPlayStartTime = SystemClock.elapsedRealtime()
        } else {
            recordWatchTime()
            lastPlayStartTime = null
        }
    }

    override fun onPositionDiscontinuity(
        eventTime: AnalyticsListener.EventTime,
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION && oldPosition.mediaItemIndex == newPosition.mediaItemIndex) {
            // 🔄 Loop detected: Standard behavior for short-form feed items
            loopCount++
            metricsTracker.incrementCounter("player.loop.count")
            scope.launch {
                analyticsClient.logEvent(
                    message = "PlaybackAnalyticsListener",
                    eventType = EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START, // Or a dedicated LOOP event
                    customMetadata = mapOf(
                        "session_id" to sessionId,
                        "loop_index" to loopCount.toString()
                    )
                )
            }
        }
    }

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        scope.launch {
            when (state) {
                Player.STATE_READY -> {
                    val startupTime = SystemClock.elapsedRealtime() - startupStartTime
                    
                    // ⏱️ Optimization: Exclude metrics if backgrounded during buffer to avoid inflation
                    val bufferingMs = if (wasBackgroundedDuringBuffer) {
                        0L
                    } else {
                        bufferingStartedAt?.let { SystemClock.elapsedRealtime() - it } ?: 0L
                    }
                    wasBackgroundedDuringBuffer = false

                    metricsTracker.trackDuration("player.startup.duration", startupTime.milliseconds)
                    metricsTracker.trackDuration("player.buffering.duration", bufferingMs.milliseconds)

                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START,
                        customMetadata = mapOf(
                            "time_ms" to startupTime.toString(),
                            "session_id" to sessionId,
                            "buffering_ms" to bufferingMs.toString()
                        )
                    )
                    bufferingStartedAt = null
                }

                Player.STATE_BUFFERING -> {
                    bufferingStartedAt = bufferingStartedAt ?: SystemClock.elapsedRealtime()
                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_BUFFERING_START,
                        customMetadata = mapOf("session_id" to sessionId)
                    )
                }

                Player.STATE_ENDED -> {
                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_END,
                        customMetadata = mapOf("session_id" to sessionId)
                    )
                }

                else -> Unit
            }
        }
    }

    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) {
        scope.launch {
            analyticsClient.logEvent(
                message = "PlaybackAnalyticsListener",
                eventType = EventTypes.EVENT_MEDIA_PLAYER_ERROR,
                customMetadata = mapOf(
                    "session_id" to sessionId,
                    "error_type" to error.errorCode.toString()
                )
            )
        }
    }
}
