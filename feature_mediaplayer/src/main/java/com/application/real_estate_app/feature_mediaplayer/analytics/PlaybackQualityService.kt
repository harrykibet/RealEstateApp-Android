package com.application.real_estate_app.feature_mediaplayer.analytics

import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.application.real_estate_app.core.utils.system.BatteryOptimizationManager
import javax.inject.Inject
import javax.inject.Singleton

// QoS metrics (rebuffering, startup time)
@Singleton
@UnstableApi
class PlaybackQualityService @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val batteryManager: BatteryOptimizationManager
) : AnalyticsListener {

    private var startupStartTime: Long = 0L

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        when (state) {
            Player.STATE_READY -> {
                val startupTime = System.currentTimeMillis() - startupStartTime
                analyticsClient.logEvent("playback_startup", mapOf("time_ms" to startupTime))
            }
            Player.STATE_BUFFERING -> analyticsClient.logEvent("buffering_start")
        }
    }

    fun optimizePlayerConfiguration(builder: ExoPlayer.Builder): ExoPlayer.Builder {
        return if (batteryManager.shouldThrottlePerformance()) {
            builder
                .setLoadControl(AdaptiveLoadControl.DEFAULT)
                .setPriorityTaskManager(null)
                .setVideoChangeFrameRateStrategy(
                    C.VideoChangeFrameRateStrategy.DEFAULT
                )
        } else {
            builder
        }
    }


    fun attachToPlayer(player: ExoPlayer) {
        startupStartTime = System.currentTimeMillis()
        player.addAnalyticsListener(this)
    }
}