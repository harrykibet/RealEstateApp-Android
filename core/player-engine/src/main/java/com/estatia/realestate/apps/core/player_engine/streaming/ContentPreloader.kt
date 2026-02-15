package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.misc.Consts.PRIORITY_NORMAL
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
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