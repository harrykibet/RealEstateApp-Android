package com.estatia.realestate.apps.core.testing.chaos.resources

import com.estatia.realestate.apps.core.common.system.ISystemResourcesMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Controller for simulating application-level resource pressure signals and visibility states.
 * Uses reactive flows to ensure that any changes are immediately propagated to observers.
 */
class ChaosResourceController {
    
    private val _memoryPressure = MutableStateFlow<MemoryPressure>(MemoryPressure.Normal)
    val memoryPressureFlow: StateFlow<MemoryPressure> = _memoryPressure.asStateFlow()
    var memoryPressure: MemoryPressure
        get() = _memoryPressure.value
        set(value) { _memoryPressure.value = value }

    private val _cpuPressure = MutableStateFlow(ISystemResourcesMonitor.CpuPressure.Normal)
    val cpuPressureFlow: StateFlow<ISystemResourcesMonitor.CpuPressure> = _cpuPressure.asStateFlow()
    var cpuPressure: ISystemResourcesMonitor.CpuPressure
        get() = _cpuPressure.value
        set(value) { _cpuPressure.value = value }

    private val _diskPressure = MutableStateFlow(ISystemResourcesMonitor.DiskPressure.Normal)
    val diskPressureFlow: StateFlow<ISystemResourcesMonitor.DiskPressure> = _diskPressure.asStateFlow()
    var diskPressure: ISystemResourcesMonitor.DiskPressure
        get() = _diskPressure.value
        set(value) { _diskPressure.value = value }

    private val _batteryStatus = MutableStateFlow(ISystemResourcesMonitor.BatteryStatus.Healthy)
    val batteryStatusFlow: StateFlow<ISystemResourcesMonitor.BatteryStatus> = _batteryStatus.asStateFlow()
    var batteryStatus: ISystemResourcesMonitor.BatteryStatus
        get() = _batteryStatus.value
        set(value) { _batteryStatus.value = value }
    
    private val _isAppVisible = MutableStateFlow(true)
    val isAppVisibleFlow: StateFlow<Boolean> = _isAppVisible.asStateFlow()
    var isAppVisible: Boolean
        get() = _isAppVisible.value
        set(value) { _isAppVisible.value = value }

    private val _isInteractive = MutableStateFlow(true)
    val isInteractiveFlow: StateFlow<Boolean> = _isInteractive.asStateFlow()
    var isInteractive: Boolean
        get() = _isInteractive.value
        set(value) { _isInteractive.value = value }
    
    sealed interface MemoryPressure {
        data object Normal : MemoryPressure
        data object Low : MemoryPressure
        data object Critical : MemoryPressure
    }

    sealed interface DataScale {
        data object Normal : DataScale
        data object HugeDataset : DataScale
        data object HugeMedia : DataScale
    }

    private val _dataScale = AtomicReference<DataScale>(DataScale.Normal)
    var dataScale: DataScale
        get() = _dataScale.get()
        set(value) { _dataScale.set(value) }

    private val _tooManySimultaneousOperations = AtomicBoolean(false)
    var tooManySimultaneousOperations: Boolean
        get() = _tooManySimultaneousOperations.get()
        set(value) { _tooManySimultaneousOperations.set(value) }
}
