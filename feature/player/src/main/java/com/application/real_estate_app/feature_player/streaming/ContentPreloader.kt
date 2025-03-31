package com.application.real_estate_app.feature_player.streaming

import com.application.real_estate_app.core_common.interfaces.IBatteryManager
import com.application.real_estate_app.core_common.misc.Consts.PRIORITY_NORMAL
import com.application.real_estate_app.core_common.interfaces.INetworkUtils
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
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager
) {
    private val preloadQueue = LinkedBlockingQueue<String>()

    fun schedulePreload(mediaUri: String, priority: Int = PRIORITY_NORMAL) {
        if (!networkUtils.isNetworkMetered()) {
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