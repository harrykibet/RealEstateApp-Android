package com.estatia.realestate.apps.core.player_ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InteractiveOverlayEngine @Inject constructor() {

    private val _elements =
        MutableStateFlow<Map<String, InteractiveOverlayNode>>(emptyMap())

    val elements: StateFlow<Map<String, InteractiveOverlayNode>> = _elements

    fun show(element: InteractiveOverlayNode) {
        _elements.update { it + (element.id to element) }
    }

    fun hide(id: String) {
        _elements.update { it - id }
    }

    fun clear() {
        _elements.value = emptyMap()
    }
}
