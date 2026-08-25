package com.estatia.realestate.apps.core.player_engine.utils

import android.net.ConnectivityManager
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.upstream.BandwidthMeter
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.system.BatteryState
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourceController
import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourcesMonitor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
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
    private lateinit var chaosMonitor: ChaosResourcesMonitor
    private lateinit var coordinator: EnvironmentCoordinator
    private val testScope = TestScope()

    @Before
    fun setup() {
        networkStateProvider = mockk(relaxed = true) {
            every { observe() } returns MutableStateFlow(mockk(relaxed = true))
        }
        batteryManager = mockk(relaxed = true) {
            every { observeBatteryState() } returns MutableStateFlow(BatteryState.Normal(80, false))
        }
        bandwidthMeter = mockk(relaxed = true)
        connectivityManager = mockk(relaxed = true)
        
        resourceController = ChaosResourceController()
        chaosMonitor = ChaosResourcesMonitor(resourceController)
        
        coordinator = EnvironmentCoordinator(
            networkStateProvider,
            batteryManager,
            bandwidthMeter,
            connectivityManager,
            chaosMonitor
        )
    }

    @Test
    fun `coordinator reflects memory pressure from chaos monitor`() = runTest {
        coordinator.start(testScope)
        
        // 🧪 Chaos Scenario: Critical Memory Pressure
        resourceController.memoryPressure = ChaosResourceController.MemoryPressure.Critical
        chaosMonitor.sync()
        
        val state = coordinator.environment.first { it.memoryTrimLevel == 15 }
        assertEquals(15, state.memoryTrimLevel)
    }

    @Test
    fun `coordinator reflects visibility changes from chaos controller`() = runTest {
        coordinator.start(testScope)
        
        // 🧪 Chaos Scenario: App backgrounded via controller
        resourceController.isAppVisible = false
        chaosMonitor.sync()
        
        val state = coordinator.environment.first { !it.isAppVisible }
        assertEquals(false, state.isAppVisible)
    }
}
