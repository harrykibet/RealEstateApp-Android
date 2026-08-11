package com.estatia.realestate.apps.core.player_engine.utils

import android.content.ComponentCallbacks2
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdaptivePlayerPoolSizingPolicy @Inject constructor(
    private val deviceUtils: IDeviceUtils,
    private val batteryManager: IBatteryManager
) : IPlayerPoolSizingPolicy {

    override fun calculateMaxPoolSize(environmentState: EnvironmentState): Int {

        // 1. Hard throttle scenarios (Low Battery or Background)
        if (batteryManager.shouldThrottlePerformance() || !environmentState.isAppVisible) {
            return 1
        }

        // 2. Severe memory pressure from OS
        if (environmentState.memoryTrimLevel >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            return 1
        }

        // 3. Hardware Decoder Constraint (Harden against "failed to initialize" errors)
        // Reserve 1 slot for the OS/system
        val hardwareLimit = (deviceUtils.getMaxSupportedVideoDecoders() - 1).coerceAtLeast(1)

        // 4. Severe memory pressure from internal check
        val availableMemory = deviceUtils.getAvailableMemoryMB()
        if (availableMemory < 150) {
            return 1.coerceAtMost(hardwareLimit)
        }

        val deviceClassSize = when {
            deviceUtils.isLowRamDevice() -> 2

            deviceUtils.isHighEndDevice() -> {
                // Tablets or high refresh flagships can handle more
                if (deviceUtils.getRefreshRate() >= 90f) 5 else 4
            }

            deviceUtils.isMidRangeDevice() -> 3

            else -> 2
        }

        return deviceClassSize.coerceAtMost(hardwareLimit)
    }
}
