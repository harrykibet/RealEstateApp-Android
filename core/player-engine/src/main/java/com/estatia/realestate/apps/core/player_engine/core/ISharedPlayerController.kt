package com.estatia.realestate.apps.core.player_engine.core

import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import kotlinx.coroutines.flow.StateFlow

interface ISharedPlayerController {

    suspend fun play(mediaId: String, mediaType: MediaType)

    suspend fun pause()
    suspend fun getPlayer(mediaId: String, mediaType: MediaType): Player

    @OptIn(UnstableApi::class)
    suspend fun preload(mediaId: String, mediaType: MediaType): PlayerPool.ManagedPlayer
    fun observeState(): StateFlow<PlaybackStateReducer.State>
    fun shutdown()
}
