package com.estatia.realestate.apps.core.common.interfaces

interface IBatteryManager {
    fun shouldThrottlePerformance(): Boolean
    fun getRecommendedQualityLevel(maxQuality: Int): Int
    fun scheduleBackgroundTask(task: Runnable, delay: Long)
    fun cleanup()
}