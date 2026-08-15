package com.estatia.realestate.apps.core.player_engine.state

import android.os.Looper
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reducer responsible for managing the logical playback state of a single player.
 * Maps low-level ExoPlayer events to consistent UI-ready states.
 *
 * Includes a watchdog mechanism to prevent hanging in the "Buffering" state
 * when encountering corrupt or zero-duration media.
 */
class PlaybackStateReducer(
    private val scope: CoroutineScope,
    private val watchdogTimeoutMs: Long = 7_000L
) {

    /**
     * Represent the possible UI-visible states of a video player.
     */
    sealed interface State {
        data object Idle : State
        data object Buffering : State
        data object Ready : State
        data object Playing : State
        data object Paused : State
        data object Ended : State
        data object Reconnecting : State
        data class Error(val error: PlaybackException) : State
    }

    /**
     * Represents events that trigger state transitions.
     */
    sealed interface Event {
        data object Reset : Event
        data object BufferingStarted : Event
        data object BufferingCompleted : Event
        data object Play : Event
        data object Pause : Event
        data object PlaybackEnded : Event
        data object NetworkLost : Event
        data object NetworkRestored : Event
        data class PlaybackError(val error: PlaybackException) : Event
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private var watchdogJob: Job? = null

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("PlaybackStateReducer must only be accessed from the Main thread.")
        }
    }

    fun dispatch(event: Event) {
        scope.launch(Dispatchers.Main.immediate) {
            checkConfinement()
            _state.value = reduce(event)
            handleWatchdog(event)
        }
    }

    private fun reduce(event: Event): State {
        return when (event) {
            Event.Reset -> State.Idle
            Event.BufferingStarted -> State.Buffering
            Event.BufferingCompleted -> State.Ready
            Event.Play -> State.Playing
            Event.Pause -> State.Paused
            Event.PlaybackEnded -> State.Ended
            Event.NetworkLost -> State.Reconnecting
            Event.NetworkRestored -> State.Buffering
            is Event.PlaybackError -> State.Error(event.error)
        }
    }

    private fun handleWatchdog(event: Event) {
        when (event) {
            Event.BufferingStarted, Event.NetworkRestored -> {
                startWatchdog()
            }
            Event.BufferingCompleted, is Event.PlaybackError, Event.Reset, Event.PlaybackEnded -> {
                stopWatchdog()
            }
            else -> Unit
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private fun startWatchdog() {
        stopWatchdog()
        watchdogJob = scope.launch {
            delay(watchdogTimeoutMs.milliseconds)
            // If we reached here, it means we've been buffering for too long.
            // Dispatch a synthetic error to unblock the UI.
            dispatch(
                Event.PlaybackError(
                    PlaybackException(
                        "Playback attempt timed out (Watchdog)",
                        null,
                        PlaybackException.ERROR_CODE_UNSPECIFIED
                    )
                )
            )
        }
    }

    private fun stopWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = null
    }
}
