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
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
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

    private var currentMediaId: String? = null

    private var playJob: Job? = null
    private var preloadJob: Job? = null

    private val warmedMedia = mutableSetOf<String>()

    private val _uiState =
        MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)

    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        observeEngineState()
    }

    private fun observeEngineState() {
        viewModelScope.launch {
            playerController.observeState()
                .collectLatest { engineState ->

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

    fun onPageVisible(context: FeedMediaContext) {

        if (currentMediaId == context.mediaId) return

        currentMediaId = context.mediaId

        playJob?.cancel()
        preloadJob?.cancel()

        playJob = viewModelScope.launch {
            playerController.play(
                mediaId = context.mediaId,
                mediaType = MediaType.VOD
            )
        }

        warmVisible(context.mediaId, context.uri)

        preloadJob = viewModelScope.launch {

            context.previous?.let {
                playerController.preload(it.mediaId, MediaType.VOD)
            }

            context.next?.let {
                playerController.preload(it.mediaId, MediaType.VOD)
                warmNext(it.mediaId, it.uri)
            }
        }
    }

    private fun warmVisible(mediaId: String, uri: Uri) {
        if (warmedMedia.add(mediaId)) {
            streamingPipeline.warm(uri, WarmPriority.VISIBLE)
        }
    }

    private fun warmNext(mediaId: String, uri: Uri) {
        if (warmedMedia.add(mediaId)) {
            streamingPipeline.warm(uri, WarmPriority.NEXT)
        }
    }

    suspend fun getPlayer(
        mediaId: String,
        mediaType: MediaType
    ): Player {
        return playerController.getPlayer(mediaId, mediaType)
    }

    fun pause() {
        viewModelScope.launch {
            playerController.pause()
        }
    }

    fun isActive(mediaId: String): Boolean {
        return currentMediaId == mediaId
    }

    private fun mapToUiState(state: PlaybackStateReducer.State): PlayerUiState {
        return when (state) {
            PlaybackStateReducer.State.Idle -> PlayerUiState.Idle
            PlaybackStateReducer.State.Buffering -> PlayerUiState.Buffering
            PlaybackStateReducer.State.Ready -> PlayerUiState.Ready
            PlaybackStateReducer.State.Playing -> PlayerUiState.Playing
            PlaybackStateReducer.State.Paused -> PlayerUiState.Paused
            PlaybackStateReducer.State.Ended -> PlayerUiState.Ended
            is PlaybackStateReducer.State.Error ->
                PlayerUiState.Error(state.error.message)
        }
    }

    override fun onCleared() {
        warmedMedia.clear()
        super.onCleared()
    }
}