package com.estatia.realestate.apps.core.testing.chaos.resources

import com.estatia.realestate.apps.core.common.system.ISystemResourcesMonitor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ChaosResourceControllerTest {

    private val controller = ChaosResourceController()

    @Test
    fun memoryPressure_updatesFlow() = runTest {
        controller.memoryPressure = ChaosResourceController.MemoryPressure.Critical
        assertEquals(ChaosResourceController.MemoryPressure.Critical, controller.memoryPressureFlow.value)
    }

    @Test
    fun cpuPressure_updatesFlow() = runTest {
        controller.cpuPressure = ISystemResourcesMonitor.CpuPressure.Throttled
        assertEquals(ISystemResourcesMonitor.CpuPressure.Throttled, controller.cpuPressureFlow.value)
    }

    @Test
    fun diskPressure_updatesFlow() = runTest {
        controller.diskPressure = ISystemResourcesMonitor.DiskPressure.Exhausted
        assertEquals(ISystemResourcesMonitor.DiskPressure.Exhausted, controller.diskPressureFlow.value)
    }

    @Test
    fun batteryStatus_updatesFlow() = runTest {
        controller.batteryStatus = ISystemResourcesMonitor.BatteryStatus.ThermalWarning
        assertEquals(ISystemResourcesMonitor.BatteryStatus.ThermalWarning, controller.batteryStatusFlow.value)
    }

    @Test
    fun isAppVisible_updatesFlow() = runTest {
        controller.isAppVisible = false
        assertEquals(false, controller.isAppVisibleFlow.value)
    }

    @Test
    fun isInteractive_updatesFlow() = runTest {
        controller.isInteractive = false
        assertEquals(false, controller.isInteractiveFlow.value)
    }
}
