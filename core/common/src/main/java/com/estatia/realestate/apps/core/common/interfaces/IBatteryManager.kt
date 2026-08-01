package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.common.system.BatteryState
import kotlinx.coroutines.flow.Flow

interface IBatteryManager {
    fun shouldThrottlePerformance(): Boolean
    fun getRecommendedQualityLevel(maxQuality: Int): Int
    fun scheduleBackgroundTask(task: Runnable, delay: Long)
    fun cleanup()
    fun observeBatteryState(): Flow<BatteryState>
}
