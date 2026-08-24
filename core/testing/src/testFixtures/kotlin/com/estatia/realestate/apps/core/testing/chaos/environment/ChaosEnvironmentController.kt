package com.estatia.realestate.apps.core.testing.chaos.environment

import com.estatia.realestate.apps.core.model.player.EnvironmentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Controller for simulating dynamic environmental changes (thermal, battery, bandwidth).
 */
class ChaosEnvironmentController(initialState: EnvironmentState) {
    
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<EnvironmentState> = _state

    /**
     * Simulates the device entering a thermal throttling state.
     */
    fun triggerHighThermal() {
        _state.value = _state.value.copy(shouldThrottlePerformance = true)
    }

    /**
     * Simulates the device being on a metered (expensive) connection.
     */
    fun triggerMeteredConnection() {
        _state.value = _state.value.copy(isMetered = true)
    }

    /**
     * Drops estimated throughput to simulate a 3G/Edge scenario.
     */
    fun simulateSlowNetwork() {
        _state.value = _state.value.copy(estimatedThroughputBps = 100_000L)
    }
    
    fun update(state: EnvironmentState) {
        _state.value = state
    }
}
