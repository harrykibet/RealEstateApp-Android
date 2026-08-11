package com.estatia.realestate.apps.core.player_engine.utils

data class EnvironmentState(
    val isMetered: Boolean,
    val shouldThrottlePerformance: Boolean,
    val estimatedThroughputBps: Long,
    val recentStallCount: Int = 0,
    val memoryTrimLevel: Int = 0,
    val isAppVisible: Boolean = true,
    val isInteractive: Boolean = true,
    val isSustainedLowBandwidth: Boolean = false,
    val thermalStatus: Int = 0
)
