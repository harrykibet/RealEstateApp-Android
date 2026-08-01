package com.estatia.realestate.apps.core.player_engine.analytics

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
// PlaybackAnalyticsListener.kt — drop implicit singleton assumption, add session correlation
@UnstableApi
class PlaybackAnalyticsListener @Inject constructor(
    private val analyticsClient: IAnalyticsTracker,
) : AnalyticsListener {

    val sessionId: String = java.util.UUID.randomUUID().toString()

    @Volatile
    private var startupStartTime: Long = 0L

    private val scope = CoroutineScope(Dispatchers.IO)

    fun markPlaybackStart() {
        startupStartTime = System.currentTimeMillis()
    }

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        scope.launch {
            when (state) {
                Player.STATE_READY -> {
                    val startupTime = System.currentTimeMillis() - startupStartTime
                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START,
                        customMetadata = mapOf(
                            "time_ms" to startupTime.toString(),
                            "session_id" to sessionId
                        )
                    )
                }
                Player.STATE_BUFFERING -> {
                    analyticsClient.logEvent(
                        message = "PlaybackAnalyticsListener",
                        eventType = EventTypes.EVENT_MEDIA_PLAYER_BUFFERING_START,
                        customMetadata = mapOf("session_id" to sessionId)
                    )
                }
            }
        }
    }
}
