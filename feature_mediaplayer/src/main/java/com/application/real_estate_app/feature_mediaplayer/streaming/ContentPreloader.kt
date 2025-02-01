package com.application.real_estate_app.feature_mediaplayer.streaming

import android.provider.CallLog.Calls.PRIORITY_NORMAL
import com.application.real_estate_app.core.utils.system.BatteryOptimizationManager
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton

// Predictive preloading
@Singleton
@androidx.media3.common.util.UnstableApi
class ContentPreloader @Inject constructor(
    private val cacheManager: CacheManager,
    private val networkUtils: NetworkUtils,
    private val batteryManager: BatteryOptimizationManager
) {
    private val preloadQueue = LinkedBlockingQueue<String>()

    fun schedulePreload(mediaUri: String, priority: Int = PRIORITY_NORMAL) {
        if (networkUtils.isConnectedToUnmeteredNetwork()) {
            preloadQueue.add(mediaUri)
            startPreloadWorker()
        }
    }

    private fun startPreloadWorker() = CoroutineScope(Dispatchers.IO).launch {
        while (preloadQueue.isNotEmpty()) {
            val uri = preloadQueue.poll()
            cacheManager.prefetch(uri!!)
        }
    }

    fun shouldPreload(): Boolean {
        return !batteryManager.shouldThrottlePerformance()
    }
}