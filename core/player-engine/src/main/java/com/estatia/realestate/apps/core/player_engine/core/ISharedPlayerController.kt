package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.Player
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import kotlinx.coroutines.flow.StateFlow

interface ISharedPlayerController {

    suspend fun play(mediaId: String, mediaType: MediaType)

    suspend fun pause()

    suspend fun preload(mediaId: String, mediaType: MediaType)

    fun observeState(): StateFlow<PlaybackState.State>

    suspend fun getPlayer(): Player
}
