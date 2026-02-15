package com.estatia.realestate.apps.core.player_ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverlayManager @Inject constructor() {

    private val _overlays =
        MutableStateFlow<Map<String, OverlayNode>>(emptyMap())

    val overlays: StateFlow<Map<String, OverlayNode>> = _overlays

    fun addOverlay(node: OverlayNode) {
        _overlays.update { it + (node.id to node) }
    }

    fun removeOverlay(id: String) {
        _overlays.update { it - id }
    }

    fun clear() {
        _overlays.value = emptyMap()
    }
}
