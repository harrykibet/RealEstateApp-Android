package com.estatia.realestate.apps.feature.search.ui.viewmodels.playback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.core.FeedNeighborInfo
import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchVideoPlaybackViewModel @Inject constructor(
    private val coordinator: VideoPlaybackCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        coordinator.observeState()
            .onEach { engineState ->
                when (engineState) {
                    PlaybackStateReducer.State.Buffering -> coordinator.onBufferingStarted()
                    PlaybackStateReducer.State.Ready, PlaybackStateReducer.State.Playing -> coordinator.onBufferingEnded()
                    else -> Unit
                }
                _uiState.value = mapToUiState(engineState)
            }
            .launchIn(viewModelScope)
    }

    fun onPageVisible(context: FeedMediaContext) {
        coordinator.onPageVisible(
            scope = viewModelScope,
            mediaId = context.mediaId,
            uri = context.uri,
            previous = context.previous?.let { FeedNeighborInfo(it.mediaId, it.uri) },
            next = context.next?.let { FeedNeighborInfo(it.mediaId, it.uri) }
        )
    }

    suspend fun getPlayer(mediaId: String, mediaType: MediaType): Player =
        coordinator.getPlayer(mediaId, mediaType)

    fun pause() = coordinator.pause(viewModelScope)

    fun isMediaActive(mediaId: String): Boolean = coordinator.isMediaActive(mediaId)

    private fun mapToUiState(state: PlaybackStateReducer.State): PlayerUiState {
        return when (state) {
            PlaybackStateReducer.State.Idle -> PlayerUiState.Idle
            PlaybackStateReducer.State.Buffering -> PlayerUiState.Buffering
            PlaybackStateReducer.State.Ready -> PlayerUiState.Ready
            PlaybackStateReducer.State.Playing -> PlayerUiState.Playing
            PlaybackStateReducer.State.Paused -> PlayerUiState.Paused
            PlaybackStateReducer.State.Ended -> PlayerUiState.Ended
            is PlaybackStateReducer.State.Error -> PlayerUiState.Error(state.error.message)
        }
    }

    override fun onCleared() {
        coordinator.clear()
    }
}
