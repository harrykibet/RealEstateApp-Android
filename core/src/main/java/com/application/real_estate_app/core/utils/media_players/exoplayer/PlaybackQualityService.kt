package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.app.Service
import android.content.Intent
import android.os.Binder
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.application.real_estate_app.core.common.events.EventType
import com.application.real_estate_app.core.domain.interfaces.AnalyticsApiInterface
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@UnstableApi
class PlaybackQualityService : Service() {
    @Inject
    lateinit var analytics: AnalyticsApiInterface
    @Inject
    lateinit var logger: LoggerInterface

    private val binder = PlaybackQualityBinder()

    inner class PlaybackQualityBinder : Binder() {
        fun getService() = this@PlaybackQualityService
    }

    override fun onBind(intent: Intent) = binder

    fun startQualityMonitoring(player: MediaPlayer) {
        val qualityMonitor = object : AnalyticsListener {
            override fun onDroppedVideoFrames(
                eventTime: AnalyticsListener.EventTime,
                droppedFrames: Int,
                elapsedMs: Long
            ) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Call the suspend function in a coroutine
                        analytics.logEvent(
                            "Dropped Video Frames: $droppedFrames",
                            EventType.EVENT_MEDIA_PLAYER_QUALITY_MONITORING,
                            null
                        ) { exception ->
                            exception.message?.let { logger.error(it) }
                        }
                    } catch (e: Exception) {
                        // Handle any exceptions during the log event call
                        withContext(Dispatchers.Main) {
                            logger.error("Failed to log event: ${e.message}")
                        }
                    }
                }
            }
        }
        player.exoPlayer.addAnalyticsListener(qualityMonitor)
    }
}