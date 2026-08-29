package com.estatia.realestate.apps.core.player_engine.utils

import android.net.ConnectivityManager
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.system.BatteryState
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourceController
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourcesMonitor
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class EnvironmentCoordinatorChaosTest {

    private lateinit var networkStateProvider: INetworkStateProvider
    private lateinit var batteryManager: IBatteryManager
    private lateinit var bandwidthMeter: BandwidthMeter
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var resourceController: ChaosResourceController

    @Before
    fun setup() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockk(relaxed = true)

        networkStateProvider = mockk(relaxed = true) {
            every { observe() } returns MutableStateFlow(mockk(relaxed = true))
        }
        batteryManager = mockk(relaxed = true) {
            every { observeBatteryState() } returns MutableStateFlow(BatteryState.Normal(80, false))
        }
        bandwidthMeter = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        
        resourceController = ChaosResourceController()
    }

    @After
    fun tearDown() {
        unmockkStatic(Looper::class)
    }

    @Test
    fun `coordinator reflects memory pressure from chaos monitor`() = runTest {
        val chaosMonitor = ChaosResourcesMonitor(resourceController, backgroundScope)
        val coordinator = createCoordinator(chaosMonitor)
        coordinator.start(backgroundScope)
        
        // 🧪 Chaos Scenario: Critical Memory Pressure
        resourceController.memoryPressure = ChaosResourceController.MemoryPressure.Critical
        
        val state = coordinator.environment.first { it.memoryTrimLevel == 15 }
        assertEquals(15, state.memoryTrimLevel)
    }

    @Test
    fun `coordinator reflects visibility changes from chaos controller`() = runTest {
        val chaosMonitor = ChaosResourcesMonitor(resourceController, backgroundScope)
        val coordinator = createCoordinator(chaosMonitor)
        coordinator.start(backgroundScope)
        
        // 🧪 Chaos Scenario: App backgrounded via controller
        resourceController.isAppVisible = false
        
        val state = coordinator.environment.first { !it.isAppVisible }
        assertEquals(false, state.isAppVisible)
    }

    private fun createCoordinator(chaosMonitor: ChaosResourcesMonitor) = EnvironmentCoordinator(
        networkStateProvider,
        batteryManager,
        bandwidthMeter,
        connectivityManager,
        chaosMonitor
    )
}
