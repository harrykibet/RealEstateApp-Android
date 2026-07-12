package com.estatia.realestate.apps.core.player_engine.analytics

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsTracker
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
@UnstableApi
class PlaybackAnalyticsListener @Inject constructor(
    private val analyticsClient: IAnalyticsTracker,
    private val logger: LoggerInterface
) : AnalyticsListener {

    private var startupStartTime: Long = 0L
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Must be called before playback starts to measure startup time.
     */
    fun markPlaybackStart() {
        startupStartTime = System.currentTimeMillis()
    }

    override fun onPlaybackStateChanged(
        eventTime: AnalyticsListener.EventTime,
        state: Int
    ) {
        scope.launch {
            when (state) {

                Player.STATE_READY -> {
                    val startupTime =
                        System.currentTimeMillis() - startupStartTime

                    analyticsClient.logEvent(
                        "PlaybackAnalyticsListener",
                        EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START,
                        mapOf("time_ms" to startupTime.toString())
                    ) { exception ->
                        exception.message?.let { logger.e(it) }
                    }
                }

                Player.STATE_BUFFERING -> {
                    analyticsClient.logEvent(
                        "PlaybackAnalyticsListener",
                        EventTypes.EVENT_MEDIA_PLAYER_BUFFERING_START,
                        null
                    ) { exception ->
                        exception.message?.let { logger.e(it) }
                    }
                }
            }
        }
    }
}
