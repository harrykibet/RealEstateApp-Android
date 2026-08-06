package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.core.PlayerPool
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import kotlinx.coroutines.flow.StateFlow

interface IPlayerManager {

    suspend fun play(mediaId: String, uri: Uri, mediaType: MediaType)

    suspend fun pause()
    suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player

    @OptIn(UnstableApi::class)
    suspend fun preload(mediaId: String, uri: Uri, mediaType: MediaType): PlayerPool.ManagedPlayer
    fun observeState(): StateFlow<PlaybackStateReducer.State>
    fun shutdown()
}
