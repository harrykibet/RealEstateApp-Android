package com.estatia.realestate.apps.core.testing.chaos.resources

import com.estatia.realestate.apps.core.common.system.ISystemResourcesMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Adversarial implementation of [ISystemResourcesMonitor] driven by [ChaosResourceController].
 */
class ChaosResourcesMonitor(
    private val controller: ChaosResourceController
) : ISystemResourcesMonitor {

    private val _memoryTrimLevel = MutableStateFlow(0)
    override val memoryTrimLevel: StateFlow<Int> = _memoryTrimLevel.asStateFlow()

    private val _isAppVisible = MutableStateFlow(true)
    override val isAppVisible: StateFlow<Boolean> = _isAppVisible.asStateFlow()

    private val _isInteractive = MutableStateFlow(true)
    override val isInteractive: StateFlow<Boolean> = _isInteractive.asStateFlow()

    /**
     * Updates the monitor flows based on the controller's current state.
     */
    fun sync() {
        _memoryTrimLevel.value = when (controller.memoryPressure) {
            ChaosResourceController.MemoryPressure.Normal -> 0
            ChaosResourceController.MemoryPressure.Low -> 10 // TRIM_MEMORY_RUNNING_LOW
            ChaosResourceController.MemoryPressure.Critical -> 15 // TRIM_MEMORY_RUNNING_CRITICAL
        }
        
        _isAppVisible.value = controller.isAppVisible
        _isInteractive.value = controller.isInteractive
    }
}
