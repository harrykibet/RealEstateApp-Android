package com.estatia.realestate.apps.core.player_engine.utils

data class EnvironmentState(
    val isMetered: Boolean,
    val shouldThrottlePerformance: Boolean,
    val estimatedThroughputBps: Long
)