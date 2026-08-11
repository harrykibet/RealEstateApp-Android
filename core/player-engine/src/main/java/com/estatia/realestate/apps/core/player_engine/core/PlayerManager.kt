package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.network.core.NetworkState
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
internal class PlayerManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentManager: PlayerEnvironmentManager,
    private val audioFocusManager: AudioFocusManager,
    private val dynamicBitrateController: DynamicBitrateController,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val networkStateProvider: INetworkStateProvider,
    private val streamingPipeline: IStreamingPipeline,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    private var activeMediaId: String? = null
    private val activeMediaIdFlow = MutableStateFlow<String?>(null)
    private val attachedPlayers = mutableSetOf<ExoPlayer>()
    private var wasPlayingBeforePause: Boolean = false

    init {
        audioFocusManager.setCallbacks(
            onLost = { pauseCurrentPlayer() },
            onGained = { resumeCurrentPlayer() }
        )
        environmentManager.start(
            onAppBackgrounded = {
                wasPlayingBeforePause = isCurrentlyPlaying()
                pause()
                pool.notifyAppBackgrounded()
            },
            onAppForegrounded = {
                if (wasPlayingBeforePause) {
                    resumeCurrentPlayer()
                }
            }
        )

        // Robust Network Recovery: Auto-retry when connection returns
        engineScope.launch(playerDispatcher) {
            networkStateProvider.observe().collect { state ->
                if (state is NetworkState.Connected) {
                    pool.forEachPlayer { player, _ ->
                        val mediaId = pool.getMediaId(player)
                        if (mediaId != null) {
                            val managed = pool.get(mediaId)
                            if (managed?.reducer?.state?.value is PlaybackStateReducer.State.Reconnecting) {
                                player.prepare()
                                managed.reducer.dispatch(PlaybackStateReducer.Event.NetworkRestored)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun play(mediaId: String, uri: Uri, mediaType: MediaType, forceLegacy: Boolean) =
        withContext(playerDispatcher) {
            val managed = pool.getOrCreate(mediaId, uri, mediaType, forceLegacy)
            val environment = environmentCoordinator.environment.value

            if (activeMediaId != mediaId) {
                activeMediaId?.let { previous -> pool.get(previous)?.player?.pause() }
            }

            attachListenerIfNeeded(managed)
            managed.analyticsListener.markPlaybackStart()
            dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
            managed.player.playWhenReady = true
            managed.player.prepare()
            managed.player.play()
            
            audioFocusManager.request()
            
            activeMediaId = mediaId
            activeMediaIdFlow.value = mediaId
            environmentManager.updateActiveMediaId(mediaId)
            
            streamingPipeline.warm(mediaId, uri, WarmPriority.VISIBLE)
        }

    @OptIn(UnstableApi::class)
    override suspend fun preload(mediaId: String, uri: Uri, mediaType: MediaType, forceLegacy: Boolean): ManagedPlayer? =
        withContext(playerDispatcher) {
            try {
                val managed = pool.prewarm(mediaId, uri, mediaType, forceLegacy)
                val environment = environmentCoordinator.environment.value
                dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
                attachListenerIfNeeded(managed)
                managed
            } catch (_: CancellationException) {
                null
            }
        }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            pauseCurrentPlayer()
            audioFocusManager.abandon()
        }
    }

    override suspend fun getPlayer(mediaId: String, uri: Uri, mediaType: MediaType): Player =
        withContext(playerDispatcher) { pool.getOrCreate(mediaId, uri, mediaType).player }

    override fun observeState(mediaId: String): Flow<PlaybackStateReducer.State> =
        pool.observeMediaState(mediaId)
            .flowOn(playerDispatcher)

    override fun shutdown() {
        engineScope.launch(playerDispatcher) {
            pauseCurrentPlayer()
            audioFocusManager.abandon()
            pool.releaseAll()
            environmentManager.stop()
        }
    }

    override fun isPlaying(): Boolean {
        return isCurrentlyPlaying()
    }

    private fun pauseCurrentPlayer() {
        activeMediaId?.let { pool.get(it)?.player?.pause() }
    }

    private fun isCurrentlyPlaying(): Boolean {
        return activeMediaId?.let { pool.get(it)?.player?.isPlaying } ?: false
    }

    private fun resumeCurrentPlayer() {
        activeMediaId?.let { id ->
            pool.get(id)?.player?.let { player ->
                if (!player.isPlaying) {
                    player.play()
                }
            }
        }
    }

    private fun isNetworkError(error: PlaybackException): Boolean {
        return error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED ||
                error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
    }

    private fun attachListenerIfNeeded(managed: ManagedPlayer) {
        if (!attachedPlayers.add(managed.player)) return

        managed.player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> managed.reducer.dispatch(PlaybackStateReducer.Event.Reset)
                    Player.STATE_BUFFERING -> managed.reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
                    Player.STATE_READY -> managed.reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
                    Player.STATE_ENDED -> managed.reducer.dispatch(PlaybackStateReducer.Event.PlaybackEnded)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                managed.reducer.dispatch(
                    if (isPlaying) PlaybackStateReducer.Event.Play else PlaybackStateReducer.Event.Pause
                )
            }

            override fun onPlayerError(error: PlaybackException) {
                engineScope.launch {
                    val isNetworkError = isNetworkError(error)
                    val networkState = networkStateProvider.current()
                    
                    if (isNetworkError && networkState !is NetworkState.Connected) {
                        managed.reducer.dispatch(PlaybackStateReducer.Event.NetworkLost)
                    } else {
                        managed.reducer.dispatch(PlaybackStateReducer.Event.PlaybackError(error))
                    }
                }
            }

            override fun onRenderedFirstFrame() {
                val bufferSeconds = (managed.player.bufferedPosition - managed.player.currentPosition) / 1000.0
                dynamicBitrateController.apply(
                    managed.player,
                    managed.mediaType,
                    environmentCoordinator.environment.value,
                    bufferSeconds = bufferSeconds,
                    startupPhase = false
                )
            }
        })
    }
}
