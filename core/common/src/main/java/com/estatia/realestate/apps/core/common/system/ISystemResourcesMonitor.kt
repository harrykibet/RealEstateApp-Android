package com.estatia.realestate.apps.core.common.system

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for monitoring system resource signals.
 */
interface ISystemResourcesMonitor {
    val memoryTrimLevel: StateFlow<Int>
    val isAppVisible: StateFlow<Boolean>
    val isInteractive: StateFlow<Boolean>

    val cpuPressure: StateFlow<CpuPressure>
    val diskPressure: StateFlow<DiskPressure>
    val batteryStatus: StateFlow<BatteryStatus>

    enum class CpuPressure { Normal, High, Throttled }
    enum class DiskPressure { Normal, Low, Exhausted }
    enum class BatteryStatus { Healthy, Low, ThermalWarning }
}
