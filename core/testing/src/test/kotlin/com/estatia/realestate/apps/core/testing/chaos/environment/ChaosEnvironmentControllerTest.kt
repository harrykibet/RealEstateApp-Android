package com.estatia.realestate.apps.core.testing.chaos.environment

import com.estatia.realestate.apps.core.model.player.EnvironmentState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChaosEnvironmentControllerTest {

    private val initialState = EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 10_000_000L
    )
    private val controller = ChaosEnvironmentController(initialState)

    @Test
    fun triggerHighThermal_updatesState() = runTest {
        controller.triggerHighThermal()
        assertTrue(controller.state.value.shouldThrottlePerformance)
    }

    @Test
    fun triggerMeteredConnection_updatesState() = runTest {
        controller.triggerMeteredConnection()
        assertTrue(controller.state.value.isMetered)
    }

    @Test
    fun simulateSlowNetwork_updatesState() = runTest {
        controller.simulateSlowNetwork()
        assertEquals(100_000L, controller.state.value.estimatedThroughputBps)
    }

    @Test
    fun update_replacesState() = runTest {
        val newState = EnvironmentState(
            isMetered = true,
            shouldThrottlePerformance = true,
            estimatedThroughputBps = 50_000L,
            recentStallCount = 5
        )
        controller.update(newState)
        assertEquals(newState, controller.state.value)
    }
}
