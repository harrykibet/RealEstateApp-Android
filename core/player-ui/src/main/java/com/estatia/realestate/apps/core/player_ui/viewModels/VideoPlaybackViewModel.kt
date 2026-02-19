package com.estatia.realestate.apps.core.player_ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val playerController: ISharedPlayerController
) : ViewModel() {

    // Track currently active media to prevent redundant play calls
    private var currentMediaId: String? = null

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

    fun preload(mediaId: String, mediaType: MediaType) {
        viewModelScope.launch {
            playerController.preload(mediaId, mediaType)
        }
    }

    suspend fun getPlayer(mediaId: String): Player {
        return playerController.getPlayer(mediaId)
    }

    fun observeState() = playerController.observeState()

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

            // Preload adjacent items regardless of currentMediaId
            previousMediaId?.let { playerController.preload(it, mediaType) }
            nextMediaId?.let { playerController.preload(it, mediaType) }
        }
    }
}
