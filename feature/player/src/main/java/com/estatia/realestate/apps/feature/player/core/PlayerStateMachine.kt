package com.estatia.realestate.apps.feature.player.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Playback state handling
@Suppress("Unused")
class PlayerStateMachine {
    sealed class State {
        data object Idle : State()
        data object Buffering : State()
        data object Ready : State()
        data class Error(val exception: Exception) : State()
    }

    sealed class Event {
        data object Initialize : Event()
        data object BufferComplete : Event()
        data class PlaybackError(val exception: Exception) : Event()
    }

    private val _currentState = MutableStateFlow<State>(State.Idle)
    val currentState: StateFlow<State> = _currentState

    fun transition(event: Event) {
        _currentState.value = when (val state = _currentState.value) {
            is State.Idle -> when (event) {
                Event.Initialize -> State.Buffering
                else -> state
            }
            is State.Buffering -> when (event) {
                Event.BufferComplete -> State.Ready
                is Event.PlaybackError -> State.Error(event.exception)
                else -> state
            }
            else -> state
        }
    }
}