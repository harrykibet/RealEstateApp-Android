package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaybackStateReducer {

    sealed interface State {
        data object Idle : State
        data object Buffering : State
        data object Ready : State
        data object Playing : State
        data object Paused : State
        data object Ended : State
        data class Error(val error: PlaybackException) : State
    }

    sealed interface Event {
        data object Reset : Event
        data object BufferingStarted : Event
        data object BufferingCompleted : Event
        data object Play : Event
        data object Pause : Event
        data object PlaybackEnded : Event
        data class PlaybackError(val error: PlaybackException) : Event
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    fun dispatch(event: Event) {
        _state.value = reduce(_state.value, event)
    }

    private fun reduce(current: State, event: Event): State {
        return when (event) {
            Event.Reset -> State.Idle
            Event.BufferingStarted -> State.Buffering
            Event.BufferingCompleted -> State.Ready
            Event.Play -> State.Playing
            Event.Pause -> State.Paused
            Event.PlaybackEnded -> State.Ended
            is Event.PlaybackError -> State.Error(event.error)
        }
    }
}