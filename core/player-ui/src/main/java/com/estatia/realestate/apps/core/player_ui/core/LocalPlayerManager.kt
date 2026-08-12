package com.estatia.realestate.apps.core.player_ui.core

import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager

/**
 * CompositionLocal for providing the [IPlayerManager] instance to the UI tree.
 */
val LocalPlayerManager = staticCompositionLocalOf<IPlayerManager> {
    error("No PlayerManager provided")
}
