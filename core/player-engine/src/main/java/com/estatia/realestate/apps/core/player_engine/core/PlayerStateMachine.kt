package com.estatia.realestate.apps.core.player_engine.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicReference

class PlayerStateMachine {

    // ----------------------------
    // States
    // ----------------------------
    sealed class State {
        data object Idle : State()
        data object Buffering : State()
        data object Ready : State()
        data object Playing : State()
        data object Paused : State()
        data object Ended : State()
        data class Error(val throwable: Throwable) : State()
        data object Released : State()
    }

    // ----------------------------
    // Events
    // ----------------------------
    sealed class Event {
        data object Initialize : Event()
        data object BufferingStarted : Event()
        data object BufferingCompleted : Event()
        data object Play : Event()
        data object Pause : Event()
        data object PlaybackEnded : Event()
        data class PlaybackError(val throwable: Throwable) : Event()
        data object Release : Event()
        data object Reset : Event()
    }

    private val stateRef = AtomicReference<State>(State.Idle)
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    fun transition(event: Event) {
        val newState = reduce(stateRef.get(), event)
        stateRef.set(newState)
        _state.value = newState
    }

    private fun reduce(current: State, event: Event): State {
        return when (current) {

            State.Idle -> when (event) {
                Event.Initialize -> State.Buffering
                Event.Release -> State.Released
                else -> current
            }

            State.Buffering -> when (event) {
                Event.BufferingCompleted -> State.Ready
                is Event.PlaybackError -> State.Error(event.throwable)
                Event.Release -> State.Released
                else -> current
            }

            State.Ready -> when (event) {
                Event.Play -> State.Playing
                Event.Release -> State.Released
                else -> current
            }

            State.Playing -> when (event) {
                Event.Pause -> State.Paused
                Event.PlaybackEnded -> State.Ended
                is Event.PlaybackError -> State.Error(event.throwable)
                Event.Release -> State.Released
                else -> current
            }

            State.Paused -> when (event) {
                Event.Play -> State.Playing
                Event.Release -> State.Released
                else -> current
            }

            State.Ended -> when (event) {
                Event.Reset -> State.Idle
                Event.Release -> State.Released
                else -> current
            }

            is State.Error -> when (event) {
                Event.Reset -> State.Idle
                Event.Release -> State.Released
                else -> current
            }

            State.Released -> current
        }
    }
}
