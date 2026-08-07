package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.core.PlayerPool
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Main entry point for the media playback engine.
 * Responsible for high-level player lifecycle, state observation, and resource management.
 */
interface IPlayerManager {

    /**
     * Prepares and starts playback for the specified media.
     *
     * @param mediaId Unique identifier for the media asset.
     * @param uri The source URI for the media.
     * @param mediaType The type of media (e.g., LIVE or VOD).
     */
    suspend fun play(mediaId: String, uri: Uri, mediaType: MediaType)

    /**
     * Pauses the currently active player.
     */
    suspend fun pause()

    /**
     * Acquires an ExoPlayer instance for the given media ID.
     * Reuses existing pooled instances if available.
     *
     * @param mediaId Unique identifier for the media asset.
     * @param uri The source URI for the media.
     * @param mediaType The type of media.
     * @return An active [Player] instance.
     */
    suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player

    /**
     * Prefetches media content into the cache and prepares a player instance in the background.
     *
     * @param mediaId Unique identifier for the media asset.
     * @param uri The source URI for the media.
     * @param mediaType The type of media.
     * @return A [PlayerPool.ManagedPlayer] container holding the pre-prepared player.
     */
    @OptIn(UnstableApi::class)
    suspend fun preload(mediaId: String, uri: Uri, mediaType: MediaType): PlayerPool.ManagedPlayer

    /**
     * Returns a [Flow] observing the playback state for a specific media item.
     * The flow will emit [PlaybackStateReducer.State.Idle] if the media is not currently loaded.
     *
     * @param mediaId Unique identifier for the media asset to observe.
     */
    fun observeState(mediaId: String): Flow<PlaybackStateReducer.State>

    /**
     * Releases all managed players and stops background orchestration services.
     */
    fun shutdown()
}
