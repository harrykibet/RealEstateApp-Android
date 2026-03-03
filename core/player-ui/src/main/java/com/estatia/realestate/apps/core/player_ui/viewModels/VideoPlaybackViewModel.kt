package com.estatia.realestate.apps.core.player_ui.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.IPlayerManager
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val playerController: IPlayerManager,
    private val streamingPipeline: IStreamingPipeline
) : ViewModel() {

    // ---------------------------------------
    // Active Media Tracking
    // ---------------------------------------

    private var currentMediaId: String? = null
    private var playJob: Job? = null
    private var preloadJob: Job? = null

    // Track what we’ve already warmed to avoid redundant warm calls
    private val warmedMedia = mutableSetOf<String>()

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

                    // Forward buffering state to cache warmer
                    when (engineState) {
                        PlaybackStateReducer.State.Buffering ->
                            streamingPipeline.onBufferingStarted()

                        PlaybackStateReducer.State.Ready,
                        PlaybackStateReducer.State.Playing ->
                            streamingPipeline.onBufferingEnded()

                        else -> Unit
                    }

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

    fun play(
        mediaId: String,
        mediaType: MediaType,
        mediaUri: Uri
    ) {
        if (currentMediaId == mediaId) return

        currentMediaId = mediaId

        playJob?.cancel()
        playJob = viewModelScope.launch {
            playerController.play(mediaId, mediaType)
        }

        warmVisible(mediaId, mediaUri)
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
        mediaUri: Uri,
        previous: Pair<String, Uri>?,
        next: Pair<String, Uri>?
    ) {

        if (currentMediaId == mediaId) return

        currentMediaId = mediaId

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = viewModelScope.launch {
            playerController.play(mediaId, mediaType)
        }

        // Warm visible immediately
        warmVisible(mediaId, mediaUri)

        preloadJob = viewModelScope.launch {

            // Preload players (player-level)
            previous?.let {
                playerController.preload(it.first, mediaType)
            }

            next?.let {
                playerController.preload(it.first, mediaType)
            }

            // Warm cache (network-level)
            next?.let {
                warmNext(it.first, it.second)
            }
        }
    }

    // ------------------------------------------------------------
    // Cache Warming
    // ------------------------------------------------------------

    private fun warmVisible(
        mediaId: String,
        uri: Uri
    ) {
        if (warmedMedia.add(mediaId)) {
            streamingPipeline.warm(uri, WarmPriority.VISIBLE)
        }
    }

    private fun warmNext(
        mediaId: String,
        uri: Uri
    ) {
        if (warmedMedia.add(mediaId)) {
            streamingPipeline.warm(uri, WarmPriority.NEXT)
        }
    }

    override fun onCleared() {
        super.onCleared()
        warmedMedia.clear()
    }
}