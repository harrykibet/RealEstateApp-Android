package com.estatia.realestate.apps.core.testing.chaos.resources

/**
 * Controller for simulating application-level resource pressure signals.
 */
class ChaosResourceController {
    
    var memoryPressure: MemoryPressure = MemoryPressure.Normal
    var cpuPressure: CpuPressure = CpuPressure.Normal
    
    sealed interface MemoryPressure {
        data object Normal : MemoryPressure
        data object Low : MemoryPressure
        data object Critical : MemoryPressure
    }

    sealed interface CpuPressure {
        data object Normal : CpuPressure
        data object High : CpuPressure
        data object ThermalThrottling : CpuPressure
    }
}
