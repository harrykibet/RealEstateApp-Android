package com.estatia.realestate.apps.feature.player.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.estatia.realestate.apps.feature.player.state.PlayerUiState
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer
import kotlinx.coroutines.isActive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoplayer: IExoplayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var progressJob: Job? = null

    fun togglePlayback() {
        val current = _uiState.value
        _uiState.value = current.copy(
            isPlaying = !current.isPlaying
        )

        if (_uiState.value.isPlaying) {
            observePlayer()
        }
    }

    fun seekTo(positionMs: Long) {
        val current = _uiState.value
        _uiState.value = current.copy(
            positionMs = positionMs.coerceIn(0L, current.durationMs)
        )
    }

    fun setDuration(durationMs: Long) {
        _uiState.value = _uiState.value.copy(
            durationMs = durationMs
        )
    }

    fun seekToFraction(fraction: Float) {
        val current = _uiState.value
        val positionMs = (fraction * current.durationMs).toLong()
        seekTo(positionMs)
    }

    fun loadMedia(mediaId: String) {
        exoplayer.preloadMedia(mediaId)
    }


    fun observePlayer() {
        val player = exoplayer.getCurrentPlayer() ?: return

        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (isActive) {
                val duration = player.duration.takeIf { it > 0 } ?: 1L
                val position = player.currentPosition

                _uiState.value = _uiState.value.copy(
                    isPlaying = player.isPlaying,
                    durationMs = duration,
                    positionMs = position
                )

                delay(500) // smooth but efficient
            }
        }
    }
}
