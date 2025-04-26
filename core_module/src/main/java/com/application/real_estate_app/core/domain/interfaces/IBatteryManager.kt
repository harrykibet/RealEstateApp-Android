package com.application.real_estate_app.core.domain.interfaces

interface IBatteryManager {
    fun shouldThrottlePerformance(): Boolean
    fun getRecommendedQualityLevel(maxQuality: Int): Int
    fun scheduleBackgroundTask(task: Runnable, delay: Long)
    fun cleanup()
}