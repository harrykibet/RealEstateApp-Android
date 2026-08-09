package com.estatia.realestate.apps.core.player_ui.core

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal for providing the [SurfacePool] instance to the UI tree.
 */
val LocalSurfacePool = staticCompositionLocalOf<SurfacePool> {
    error("No SurfacePool provided")
}
