package com.estatia.realestate.apps.core.player_engine.core

import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class AdaptivePlayerPoolSizingPolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils,
    private val batteryManager: IBatteryManager
) : PlayerPoolSizingPolicy {

    override fun calculateMaxPoolSize(): Int {

        // Hard throttle scenario
        if (batteryManager.shouldThrottlePerformance()) {
            return 1
        }

        // Severe memory pressure
        val availableMemory = deviceUtils.getAvailableMemoryMB()
        if (availableMemory < 150) {
            return 1
        }

        return when {
            deviceUtils.isLowRamDevice() -> 2

            deviceUtils.isHighEndDevice() -> {
                // Tablets or high refresh flagships can handle more
                if (deviceUtils.getRefreshRate() >= 90f) 5 else 4
            }

            deviceUtils.isMidRangeDevice() -> 3

            else -> 2
        }
    }
}