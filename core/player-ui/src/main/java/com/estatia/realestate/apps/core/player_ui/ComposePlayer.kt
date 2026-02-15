package com.estatia.realestate.apps.core.player_ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player

@Composable
fun ComposePlayer(
    player: Player,
    overlayManager: OverlayManager,
    interactiveEngine: InteractiveOverlayEngine
) {
    Box {
        VideoRenderer(
            player = player,
            modifier = Modifier.matchParentSize()
        )

        PlayerOverlayLayer(overlayManager)

        InteractiveOverlayLayer(interactiveEngine)
    }
}

