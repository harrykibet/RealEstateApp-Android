package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.model.player.EnvironmentState
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
     * @param title Optional title for media session metadata.
     * @param artist Optional artist name for media session metadata.
     */
    suspend fun play(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        matchScore: Float = 0.5f,
        title: String? = null,
        artist: String? = null
    )

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
     * @param matchScore The match score from the recommendation engine (0.0 to 1.0).
     * @return An active [Player] instance.
     */
    suspend fun getPlayer(
        mediaId: String, 
        uri: MediaReference, 
        mediaType: MediaType,
        matchScore: Float = 0.5f
    ): Player

    /**
     * Prefetches media content into the cache and prepares a player instance in the background.
     *
     * @param mediaId Unique identifier for the media asset.
     * @param uri The source URI for the media.
     * @param mediaType The type of media.
     * @param matchScore The match score from the recommendation engine (0.0 to 1.0).
     * @param title Optional title for media session metadata.
     * @param artist Optional artist name for media session metadata.
     */
    @OptIn(UnstableApi::class)
    suspend fun preload(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        matchScore: Float = 0.5f,
        title: String? = null,
        artist: String? = null
    )

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

    /**
     * Returns true if a video is currently playing in the active player.
     */
    fun isPlaying(): Boolean

    /**
     * Returns true if the specified media ID is the currently active one.
     */
    fun isMediaActive(mediaId: String): Boolean

    /**
     * The ID of the currently active media item, if any.
     */
    val activeMediaId: String?

    /**
     * The current global environment state (network, battery, visibility).
     */
    val environment: StateFlow<EnvironmentState>

    /**
     * Notifies the manager that a specific media ID is currently bound to a UI component.
     * This "pins" the player in the pool to prevent eviction during scrolling.
     */
    fun notifyMediaBound(mediaId: String)

    /**
     * Notifies the manager that a specific media ID is no longer bound to a UI component.
     */
    fun notifyMediaUnbound(mediaId: String)
}
