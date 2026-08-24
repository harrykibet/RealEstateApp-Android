package com.estatia.realestate.apps.core.player_ui.core

import androidx.compose.runtime.compositionLocalOf
import com.estatia.realestate.apps.core.model.player.EnvironmentState

/**
 * CompositionLocal for providing the current [EnvironmentState] to the UI tree.
 * Uses [compositionLocalOf] instead of [staticCompositionLocalOf] to avoid 
 * full-tree recomposition on every bandwidth sample.
 */
val LocalEnvironmentState = compositionLocalOf<EnvironmentState> {
    // Default fallback state
    EnvironmentState(
        isMetered = false,
        shouldThrottlePerformance = false,
        estimatedThroughputBps = 0L
    )
}
