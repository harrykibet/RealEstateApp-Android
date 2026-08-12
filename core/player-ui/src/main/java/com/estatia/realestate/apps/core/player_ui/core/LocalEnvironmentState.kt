package com.estatia.realestate.apps.core.player_ui.core

import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState

/**
 * CompositionLocal for providing the current [EnvironmentState] to the UI tree.
 */
val LocalEnvironmentState = staticCompositionLocalOf<EnvironmentState> {
    // Default fallback state
    EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 0L
    )
}
