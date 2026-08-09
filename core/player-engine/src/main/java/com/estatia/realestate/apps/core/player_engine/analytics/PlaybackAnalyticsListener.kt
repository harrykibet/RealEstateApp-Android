package com.estatia.realestate.apps.core.player_engine.analytics

import android.os.SystemClock
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.player_engine.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : AnalyticsListener {

    val sessionId: String = java.util.UUID.randomUUID().toString()

    @Volatile
    private var startupStartTime: Long = 0L

    @Volatile
    private var bufferingStartedAt: Long? = null

    @Volatile
    private var firstFrameSentAt: Long? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Fail-silent for analytics; prevent app crash on logging failure
    }

    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher + exceptionHandler)

    fun markPlaybackStart() {
        startupStartTime = SystemClock.elapsedRealtime()
        bufferingStartedAt = null
        firstFrameSentAt = null
    }

    fun release() {
        scope.cancel()
    }

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        scope.launch {
            when (state) {
                Player.STATE_READY -> {
                    val startupTime = SystemClock.elapsedRealtime() - startupStartTime
                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START,
                        customMetadata = mapOf(
                            "time_ms" to startupTime.toString(),
                            "session_id" to sessionId,
                            "buffering_ms" to (bufferingStartedAt?.let { SystemClock.elapsedRealtime() - it } ?: 0L).toString()
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
