package com.estatia.realestate.apps.core.player_ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.core.FeedNeighborInfo
import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.player_ui.state.PlayerErrorType
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import androidx.media3.common.PlaybackException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Base ViewModel providing shared logic for video playback coordination in feeds.
 * Subclasses provide screen-specific scoping and dependency injection.
 * 
 * Manages the mapping of engine-level states to UI-friendly [PlayerUiState] and
 * handles per-screen state isolation.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
abstract class BaseVideoPlaybackViewModel(
    protected val coordinator: VideoPlaybackCoordinator,
    protected val environmentCoordinator: EnvironmentCoordinator
) : ViewModel() {

    private val activeMediaId = MutableStateFlow<String?>(null)
    private var lastMediaContext: FeedMediaContext? = null
    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Idle)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _meteredConnectionEvent = MutableSharedFlow<Unit>(replay = 0)
    val meteredConnectionEvent = _meteredConnectionEvent.asSharedFlow()

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        val engineStateFlow = activeMediaId
            .flatMapLatest { mediaId ->
                if (mediaId != null) {
                    coordinator.observeState(mediaId)
                } else {
                    flowOf(PlaybackStateReducer.State.Idle)
                }
            }

        combine(
            engineStateFlow,
            environmentCoordinator.environment
        ) { engineState, env ->
            handleEngineState(engineState)
            
            // ⏱️ Logic: If sustained low bandwidth is detected and we are actively trying to play/buffer,
            // surface the informative LowBandwidth state.
            if (env.isSustainedLowBandwidth && 
                (engineState is PlaybackStateReducer.State.Buffering || engineState is PlaybackStateReducer.State.Playing)
            ) {
                PlayerUiState.LowBandwidth
            } else {
                mapToUiState(engineState)
            }
        }
        .onEach { uiState ->
            _uiState.value = uiState
        }
        .launchIn(viewModelScope)

        environmentCoordinator.meteredConnectionDetected
            .onEach { _meteredConnectionEvent.emit(Unit) }
            .launchIn(viewModelScope)
    }

    private fun handleEngineState(engineState: PlaybackStateReducer.State) {
        when (engineState) {
            PlaybackStateReducer.State.Buffering -> coordinator.onBufferingStarted()
            PlaybackStateReducer.State.Ready, PlaybackStateReducer.State.Playing -> coordinator.onBufferingEnded()
            else -> Unit
        }
    }

    fun onPageVisible(context: FeedMediaContext) {
        lastMediaContext = context
        activeMediaId.value = context.mediaId
        coordinator.onPageVisible(
            scope = viewModelScope,
            mediaId = context.mediaId,
            uri = context.uri,
            previous = context.previous.map { FeedNeighborInfo(it.mediaId, it.uri) },
            next = context.next.map { FeedNeighborInfo(it.mediaId, it.uri) }
        )
    }

    fun retry() {
        val context = lastMediaContext ?: return
        coordinator.retry(viewModelScope, context.mediaId, context.uri)
    }

    suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player =
        coordinator.getPlayer(mediaId, uri, mediaType)

    fun pause() = coordinator.pause(viewModelScope)

    fun isMediaActive(mediaId: String): Boolean = coordinator.isMediaActive(mediaId)

    protected open fun mapToUiState(state: PlaybackStateReducer.State): PlayerUiState {
        return when (state) {
            PlaybackStateReducer.State.Idle -> PlayerUiState.Idle
            PlaybackStateReducer.State.Buffering -> PlayerUiState.Buffering
            PlaybackStateReducer.State.Reconnecting -> PlayerUiState.Reconnecting
            PlaybackStateReducer.State.Ready -> PlayerUiState.Ready
            PlaybackStateReducer.State.Playing -> PlayerUiState.Playing
            PlaybackStateReducer.State.Paused -> PlayerUiState.Paused
            PlaybackStateReducer.State.Ended -> PlayerUiState.Ended
            is PlaybackStateReducer.State.Error -> {
                val errorType = when (state.error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> PlayerErrorType.NETWORK

                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
                    PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED -> PlayerErrorType.SERVER

                    PlaybackException.ERROR_CODE_DECODER_INIT_FAILED,
                    PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED -> PlayerErrorType.DECODER

                    else -> PlayerErrorType.UNKNOWN
                }
                PlayerUiState.Error(state.error.message, errorType)
            }
        }
    }

    override fun onCleared() {
        coordinator.clear()
    }
}
