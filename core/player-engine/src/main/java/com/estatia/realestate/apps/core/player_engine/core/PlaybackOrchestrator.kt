package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The core implementation of playback operations, including listener translation and resource allocation.
 */
@UnstableApi
@Singleton
class PlaybackOrchestrator @Inject constructor(
    private val pool: PlayerPool,
    private val bitrateController: DynamicBitrateController,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val streamingPipeline: IStreamingPipeline,
    private val decoderPolicy: DecoderFallbackPolicy,
    private val networkRecovery: NetworkRecoveryCoordinator,
    private val sessionCoordinator: MediaSessionCoordinator,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) {
    private val attachedPlayers = WeakHashMap<ExoPlayer, Boolean>()
    var activeMediaId: String? = null
        private set

    suspend fun play(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        matchScore: Float,
        title: String?,
        artist: String?
    ) = withContext(playerDispatcher) {
        val forceLegacy = decoderPolicy.shouldForceLegacy(mediaId)
        val managed = pool.getOrCreate(mediaId, uri, mediaType, matchScore, forceLegacy, title, artist)
        val environment = environmentCoordinator.environment.value

        if (activeMediaId != mediaId) {
            activeMediaId?.let { previous -> pool.get(previous)?.player?.pause() }
        }

        attachListenerIfNeeded(managed)
        managed.analyticsListener.markPlaybackStart(mediaId)
        bitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
        
        sessionCoordinator.updateSession(managed.player)
        
        managed.player.playWhenReady = true
        managed.player.prepare()
        managed.player.play()
        
        activeMediaId = mediaId
        streamingPipeline.warm(mediaId, uri, WarmPriority.VISIBLE)
    }

    suspend fun preload(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        matchScore: Float,
        title: String?,
        artist: String?
    ) = withContext(playerDispatcher) {
        checkConfinement()
        val result = pool.prewarm(mediaId, uri, mediaType, matchScore, false, false, title, artist)
        if (result is PrewarmResult.Success) {
            val managed = result.managed
            val environment = environmentCoordinator.environment.value
            bitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
            attachListenerIfNeeded(managed)
        }
    }

    fun pauseCurrentPlayer() {
        checkConfinement()
        activeMediaId?.let { pool.get(it)?.player?.pause() }
    }

    fun resumeCurrentPlayer() {
        checkConfinement()
        activeMediaId?.let { id ->
            pool.get(id)?.player?.let { player ->
                if (!player.isPlaying) {
                    player.play()
                }
            }
        }
    }

    fun isCurrentlyPlaying(): Boolean {
        checkConfinement()
        return activeMediaId?.let { pool.get(it)?.player?.isPlaying } ?: false
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("PlaybackOrchestrator must only be accessed from the Main thread.")
        }
    }

    private fun attachListenerIfNeeded(managed: ManagedPlayer) {
        if (attachedPlayers.containsKey(managed.player)) return
        attachedPlayers[managed.player] = true

        managed.player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                val exoPlayer = player as? ExoPlayer ?: return
                val currentManaged = pool.getMediaId(exoPlayer)?.let { pool.get(it) } ?: return
                
                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                    when (exoPlayer.playbackState) {
                        Player.STATE_IDLE -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.Reset)
                        Player.STATE_BUFFERING -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
                        Player.STATE_READY -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
                        Player.STATE_ENDED -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.PlaybackEnded)
                    }
                }

                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    currentManaged.reducer.dispatch(
                        if (exoPlayer.isPlaying) PlaybackStateReducer.Event.Play else PlaybackStateReducer.Event.Pause
                    )
                }

                if (events.contains(Player.EVENT_PLAYER_ERROR)) {
                    val mediaIdForError = pool.getMediaId(exoPlayer)
                    exoPlayer.playerError?.let { error ->
                        engineScope.launch(playerDispatcher) {
                            if (error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED) {
                                mediaIdForError?.let { id -> decoderPolicy.recordFailure(id) }
                            }

                            val currentManagedInError = mediaIdForError?.let { pool.get(it) } ?: return@launch
                            val isNetworkError = networkRecovery.isNetworkError(error)
                            val networkState = networkRecovery.getCurrentState()
                            
                            if (isNetworkError && networkState !is NetworkState.Connected) {
                                currentManagedInError.reducer.dispatch(PlaybackStateReducer.Event.NetworkLost)
                            } else {
                                currentManagedInError.reducer.dispatch(PlaybackStateReducer.Event.PlaybackError(error))
                            }
                        }
                    }
                }
                
                if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME)) {
                    val bufferSeconds = (exoPlayer.bufferedPosition - exoPlayer.currentPosition) / 1000.0
                    bitrateController.apply(
                        exoPlayer,
                        currentManaged.mediaType,
                        environmentCoordinator.environment.value,
                        bufferSeconds = bufferSeconds,
                        startupPhase = false
                    )
                }
            }
        })
    }

    internal fun clearAttachedPlayers() {
        attachedPlayers.clear()
    }

    val debugAttachedPlayersCount: Int
        get() = attachedPlayers.size
}
