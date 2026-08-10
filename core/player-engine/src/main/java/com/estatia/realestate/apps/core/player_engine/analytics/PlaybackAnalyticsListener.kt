package com.estatia.realestate.apps.core.player_engine.analytics

import android.os.SystemClock
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
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

    fun markPlaybackStart() {
        startupStartTime = SystemClock.elapsedRealtime()
        bufferingStartedAt = null
        firstFrameSentAt = null
    }

    fun release() {
        // No-op: using shared EngineScope
    }

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        scope.launch {
            when (state) {
                Player.STATE_READY -> {
                    val startupTime = SystemClock.elapsedRealtime() - startupStartTime
                    val bufferingMs = bufferingStartedAt?.let { SystemClock.elapsedRealtime() - it } ?: 0L

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
