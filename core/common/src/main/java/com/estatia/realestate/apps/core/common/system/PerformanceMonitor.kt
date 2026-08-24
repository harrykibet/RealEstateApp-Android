package com.estatia.realestate.apps.core.common.system

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks real-time UI performance (jank) and exposes it as a reactive signal.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Monitor frame-level performance to enable load-shedding in heavy components.
 * - Concurrency: Thread-safe (Main-thread expected for frame data reporting).
 * - Performance: O(1) processing per frame; uses a sliding window (30 frames) to minimize noise.
 * - Observability: Provides the [isJanking] signal for real-time responsiveness tuning.
 */
@Singleton
class PerformanceMonitor @Inject constructor() {

    private val _isJanking = MutableStateFlow(false)
    val isJanking: StateFlow<Boolean> = _isJanking.asStateFlow()

    private var jankFrameCount = 0
    private var totalFrameCount = 0
    private val windowSize = 30 // 30 frame sliding window

    /**
     * Reports whether a specific frame was dropped (janky).
     * Derived from JankStats in the Activity.
     */
    fun reportFrameData(isJanky: Boolean) {
        totalFrameCount++
        if (isJanky) jankFrameCount++

        if (totalFrameCount >= windowSize) {
            // If more than 20% of frames in the window were janky, flag as janking
            val jankRatio = jankFrameCount.toFloat() / totalFrameCount
            _isJanking.value = jankRatio > 0.20f
            
            // Reset for next window
            jankFrameCount = 0
            totalFrameCount = 0
        }
    }
}
