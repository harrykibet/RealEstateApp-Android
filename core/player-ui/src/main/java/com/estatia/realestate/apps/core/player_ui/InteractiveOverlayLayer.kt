package com.estatia.realestate.apps.core.player_ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun InteractiveOverlayLayer(
    engine: InteractiveOverlayEngine
) {
    val elements = engine.elements.collectAsState()

    elements.value.values.forEach { node ->
        node.content()
    }
}
