package com.estatia.realestate.apps.core.player_ui.core

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager

/**
 * CompositionLocal for providing the [IPlayerManager] instance to the UI tree.
 */
// Justification: Uses IPlayerManager which is marked as UnstableApi due to Media3 exposure.
@OptIn(UnstableApi::class)
val LocalPlayerManager = staticCompositionLocalOf<IPlayerManager> {
    error("No PlayerManager provided")
}
