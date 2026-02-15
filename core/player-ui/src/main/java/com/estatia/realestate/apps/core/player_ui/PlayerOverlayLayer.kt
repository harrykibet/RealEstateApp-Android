package com.estatia.realestate.apps.core.player_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun PlayerOverlayLayer(
    overlayManager: OverlayManager
) {
    val overlays by overlayManager.overlays.collectAsState()

    overlays.values.forEach { node ->
        node.content()
    }
}
