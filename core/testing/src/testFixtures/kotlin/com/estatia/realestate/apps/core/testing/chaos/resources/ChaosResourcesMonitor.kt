package com.estatia.realestate.apps.core.testing.chaos.resources

import com.estatia.realestate.apps.core.common.system.ISystemResourcesMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Adversarial implementation of [ISystemResourcesMonitor] driven by [ChaosResourceController].
 * Automatically synchronizes with the controller's state using reactive flows.
 */
@Suppress("OPT_IN_USAGE")
class ChaosResourcesMonitor(
    controller: ChaosResourceController,
    scope: CoroutineScope = GlobalScope
) : ISystemResourcesMonitor {

    override val memoryTrimLevel: StateFlow<Int> = controller.memoryPressureFlow
        .map { pressure ->
            when (pressure) {
                ChaosResourceController.MemoryPressure.Normal -> 0
                ChaosResourceController.MemoryPressure.Low -> 10
                ChaosResourceController.MemoryPressure.Critical -> 15
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, 0)

    override val isAppVisible: StateFlow<Boolean> = controller.isAppVisibleFlow
    override val isInteractive: StateFlow<Boolean> = controller.isInteractiveFlow
    override val cpuPressure: StateFlow<ISystemResourcesMonitor.CpuPressure> = controller.cpuPressureFlow
    override val diskPressure: StateFlow<ISystemResourcesMonitor.DiskPressure> = controller.diskPressureFlow
    override val batteryStatus: StateFlow<ISystemResourcesMonitor.BatteryStatus> = controller.batteryStatusFlow
}
