package com.estatia.realestate.apps.core.player_ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController
import com.estatia.realestate.apps.core.player_engine.core.PlaybackState
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val playerController: ISharedPlayerController
) : ViewModel() {

    // ---------------------------------------
    // Active Media Tracking
    // ---------------------------------------

    private var currentMediaId: String? = null

    // ---------------------------------------
    // UI State (exposed to UI layer only)
    // ---------------------------------------

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        observeEngineState()
    }

    // ---------------------------------------
    // Observe Engine State Internally
    // ---------------------------------------

    private fun observeEngineState() {
        viewModelScope.launch {
            playerController.observeState().collect { engineState ->
                _uiState.value = mapToUiState(engineState)
            }
        }
    }

    // ---------------------------------------
    // Engine → UI State Mapper
    // ---------------------------------------

    private fun mapToUiState(
        state: PlaybackState.State
    ): PlayerUiState {
        return when (state) {

            PlaybackState.State.Idle ->
                PlayerUiState.Idle

            PlaybackState.State.Buffering ->
                PlayerUiState.Buffering

            PlaybackState.State.Ready ->
                PlayerUiState.Playing   // Ready implies playable

            PlaybackState.State.Playing ->
                PlayerUiState.Playing

            PlaybackState.State.Paused ->
                PlayerUiState.Paused

            PlaybackState.State.Ended ->
                PlayerUiState.Ended

            is PlaybackState.State.Error ->
                PlayerUiState.Error(state.throwable.message)

            PlaybackState.State.Released ->
                PlayerUiState.Idle
        }
    }

    // ------------------------------------------------------------
    // Single Video Playback
    // ------------------------------------------------------------

    fun play(mediaId: String, mediaType: MediaType) {
        if (currentMediaId == mediaId) return

        currentMediaId = mediaId

        viewModelScope.launch {
            playerController.play(mediaId, mediaType)
        }
    }

    fun pause() {
        viewModelScope.launch {
            playerController.pause()
        }
    }

    suspend fun getPlayer(mediaId: String): Player {
        return playerController.getPlayer(mediaId)
    }

    // ------------------------------------------------------------
    // Feed-Oriented Playback
    // ------------------------------------------------------------

    fun onPageVisible(
        mediaId: String,
        mediaType: MediaType,
        previousMediaId: String?,
        nextMediaId: String?
    ) {
        currentMediaId = mediaId

        viewModelScope.launch {

            // Play current
            playerController.play(mediaId, mediaType)

            // Preload adjacent
            previousMediaId?.let {
                playerController.preload(it, mediaType)
            }

            nextMediaId?.let {
                playerController.preload(it, mediaType)
            }
        }
    }
}