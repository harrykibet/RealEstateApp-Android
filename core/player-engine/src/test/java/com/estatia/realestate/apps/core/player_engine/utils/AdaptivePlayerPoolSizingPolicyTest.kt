package com.estatia.realestate.apps.core.player_engine.utils

import android.content.ComponentCallbacks2
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AdaptivePlayerPoolSizingPolicyTest {

    private lateinit var deviceUtils: IDeviceUtils
    private lateinit var batteryManager: IBatteryManager
    private lateinit var config: IPlayerTuningConfig
    private lateinit var policy: AdaptivePlayerPoolSizingPolicy

    private val defaultState = EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 10_000_000L
    )

    @Before
    fun setup() {
        deviceUtils = mockk(relaxed = true)
        batteryManager = mockk(relaxed = true)
        config = mockk {
            every { playerTuning } returns PlayerTuningConfig()
        }
        policy = AdaptivePlayerPoolSizingPolicy(deviceUtils, batteryManager, config)
    }

    @Test
    fun `returns 1 when battery throttling is active`() {
        every { batteryManager.shouldThrottlePerformance() } returns true
        val state = defaultState.copy(isAppVisible = true)

        val result = policy.calculateMaxPoolSize(state)

        assertEquals(1, result)
    }

    @Test
    fun `returns 1 when app is not visible`() {
        val state = defaultState.copy(isAppVisible = false)
        val result = policy.calculateMaxPoolSize(state)
        assertEquals(1, result)
    }

    @Test
    fun `returns 1 under critical memory pressure`() {
        // 🧪 Chaos: Simulate critical memory pressure signal from OS
        val state = defaultState.copy(
            isAppVisible = true,
            memoryTrimLevel = ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
        )
        val result = policy.calculateMaxPoolSize(state)
        assertEquals(1, result)
    }

    @Test
    fun `respects hardware decoder limits`() {
        // 🧪 Hardware Constraint Simulation
        every { deviceUtils.getMaxSupportedVideoDecoders() } returns 3 // Only 3 concurrent decoders allowed
        every { deviceUtils.isHighEndDevice() } returns true
        
        val state = defaultState.copy(isAppVisible = true)
        val result = policy.calculateMaxPoolSize(state)

        // limit = (3 - 1) = 2
        assertEquals(2, result)
    }

    @Test
    fun `returns higher capacity for high-end high-refresh devices`() {
        every { deviceUtils.isHighEndDevice() } returns true
        every { deviceUtils.getRefreshRate() } returns 120f
        every { deviceUtils.getMaxSupportedVideoDecoders() } returns 16
        every { deviceUtils.getAvailableMemoryMB() } returns 4096
        
        val state = defaultState.copy(isAppVisible = true)
        val result = policy.calculateMaxPoolSize(state)

        assertEquals(5, result)
    }
}
