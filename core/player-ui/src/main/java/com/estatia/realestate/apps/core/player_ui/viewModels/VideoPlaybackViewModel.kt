package com.estatia.realestate.apps.core.player_ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val playerController: IPlayerManager
) : ViewModel() {

    // ---------------------------------------
    // Active Media Tracking
    // ---------------------------------------

    private var currentMediaId: String? = null
    private var playJob: Job? = null
    private var preloadJob: Job? = null

    // ---------------------------------------
    // UI State
    // ---------------------------------------

    private val _uiState =
        MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)

    val uiState: StateFlow<PlayerUiState> =
        _uiState.asStateFlow()

    init {
        observeEngineState()
    }

    // ---------------------------------------
    // Engine → UI Mapping
    // ---------------------------------------

    private fun observeEngineState() {
        viewModelScope.launch {
            playerController
                .observeState()
                .collectLatest { engineState ->
                    _uiState.value = mapToUiState(engineState)
                }
        }
    }

    private fun mapToUiState(
        state: PlaybackStateReducer.State
    ): PlayerUiState {
        return when (state) {

            PlaybackStateReducer.State.Idle ->
                PlayerUiState.Idle

            PlaybackStateReducer.State.Buffering ->
                PlayerUiState.Buffering

            PlaybackStateReducer.State.Ready ->
                PlayerUiState.Ready

            PlaybackStateReducer.State.Playing ->
                PlayerUiState.Playing

            PlaybackStateReducer.State.Paused ->
                PlayerUiState.Paused

            PlaybackStateReducer.State.Ended ->
                PlayerUiState.Ended

            is PlaybackStateReducer.State.Error ->
                PlayerUiState.Error(state.error.message)
        }
    }

    // ------------------------------------------------------------
    // Single Video Playback
    // ------------------------------------------------------------

    fun play(mediaId: String, mediaType: MediaType) {
        if (currentMediaId == mediaId) return

        currentMediaId = mediaId

        playJob?.cancel()
        playJob = viewModelScope.launch {
            playerController.play(mediaId, mediaType)
        }
    }

    fun pause() {
        viewModelScope.launch {
            playerController.pause()
        }
    }

    suspend fun getPlayer(
        mediaId: String,
        mediaType: MediaType
    ): Player {
        return playerController.getPlayer(mediaId, mediaType)
    }

    // ------------------------------------------------------------
    // Feed-Oriented Playback (TikTok-style)
    // ------------------------------------------------------------

    fun onPageVisible(
        mediaId: String,
        mediaType: MediaType,
        previousMediaId: String?,
        nextMediaId: String?
    ) {

        currentMediaId = mediaId

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = viewModelScope.launch {
            playerController.play(mediaId, mediaType)
        }

        preloadJob = viewModelScope.launch {

            previousMediaId?.let {
                playerController.preload(it, mediaType)
            }

            nextMediaId?.let {
                playerController.preload(it, mediaType)
            }
        }
    }
}