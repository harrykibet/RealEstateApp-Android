package com.estatia.realestate.apps.feature.player.analytics

import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.estatia.realestate.apps.core.common.events.EventTypes
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// QoS metrics (rebuffering, startup time)
@Suppress("Unused")
@Singleton
@UnstableApi
class PlaybackQualityService @Inject constructor(
    private val analyticsClient: IAnalyticsRepository,
    private val logger: LoggerInterface,
    private val batteryManager: IBatteryManager
) : AnalyticsListener {

    private var startupStartTime: Long = 0L

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            when (state) {
                Player.STATE_READY -> {
                    val startupTime = System.currentTimeMillis() - startupStartTime
                    analyticsClient.logEvent("PlaybackQualityService",
                        EventTypes.EVENT_MEDIA_PLAYER_PLAYBACK_START,
                        mapOf("time_ms" to startupTime.toString())
                    ) { exception -> exception.message?.let { logger.e(it) } }
                }
                Player.STATE_BUFFERING -> analyticsClient.logEvent("PlaybackQualityService",
                    EventTypes.EVENT_MEDIA_PLAYER_BUFFERING_START,
                    null
                ) { exception -> exception.message?.let { logger.e(it) } }
            }
        }
    }

    fun optimizePlayerConfiguration(builder: ExoPlayer.Builder): ExoPlayer.Builder {
        return if (batteryManager.shouldThrottlePerformance()) {
            builder
                .setLoadControl(DefaultLoadControl())
                .setPriorityTaskManager(null)
                .setVideoChangeFrameRateStrategy(C.VIDEO_CHANGE_FRAME_RATE_STRATEGY_OFF)
        } else {
            builder
        }
    }

    fun attachToPlayer(player: ExoPlayer) {
        startupStartTime = System.currentTimeMillis()
        player.addAnalyticsListener(this)
    }
}
