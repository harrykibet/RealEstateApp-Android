package com.estatia.realestate.apps.core.player_engine.perfomance

import androidx.media3.common.C
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Applies performance optimizations to ExoPlayer.Builder
 * based on runtime device conditions (e.g., battery saver mode).
 *
 */
@Singleton
class PlayerPerformanceOptimizer @Inject constructor(
    private val batteryManager: IBatteryManager
) {

    /**
     * Adjusts player configuration based on battery state.
     *
     * If device is throttling performance:
     * - Disables frame rate switching
     * - Applies default load control
     * - Removes priority task manager
     */
    fun optimize(builder: ExoPlayer.Builder): ExoPlayer.Builder {

        if (!batteryManager.shouldThrottlePerformance()) {
            return builder
        }

        return builder
            .setLoadControl(DefaultLoadControl())
            .setPriorityTaskManager(null)
            .setVideoChangeFrameRateStrategy(
                C.VIDEO_CHANGE_FRAME_RATE_STRATEGY_OFF
            )
    }
}
