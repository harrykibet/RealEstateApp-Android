package com.estatia.realestate.apps.core.testing.chaos.resources

/**
 * Controller for simulating application-level resource pressure signals and visibility states.
 */
class ChaosResourceController {
    
    var memoryPressure: MemoryPressure = MemoryPressure.Normal
    var cpuPressure: CpuPressure = CpuPressure.Normal
    var threadPressure: ThreadPressure = ThreadPressure.Normal
    var poolPressure: PoolPressure = PoolPressure.Normal
    var queuePressure: QueuePressure = QueuePressure.Normal
    var diskPressure: DiskPressure = DiskPressure.Normal
    var workerPressure: WorkerPressure = WorkerPressure.Normal
    
    var isAppVisible: Boolean = true
    var isInteractive: Boolean = true
    
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

    sealed interface ThreadPressure {
        data object Normal : ThreadPressure
        data object Exhausted : ThreadPressure
    }

    sealed interface PoolPressure {
        data object Normal : PoolPressure
        data object Exhausted : PoolPressure
    }

    sealed interface QueuePressure {
        data object Normal : QueuePressure
        data object Saturated : QueuePressure
    }

    sealed interface DiskPressure {
        data object Normal : DiskPressure
        data object Exhausted : DiskPressure
    }

    sealed interface WorkerPressure {
        data object Normal : WorkerPressure
        data object Exhausted : WorkerPressure
    }

    sealed interface DataScale {
        data object Normal : DataScale
        data object HugeDataset : DataScale
        data object HugeMedia : DataScale
    }

    var dataScale: DataScale = DataScale.Normal
    var tooManySimultaneousOperations: Boolean = false
}
