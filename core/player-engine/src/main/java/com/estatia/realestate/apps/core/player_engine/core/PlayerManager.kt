package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val pool: PlayerPool,
    private val environmentManager: PlayerEnvironmentManager,
    private val audioFocusManager: AudioFocusManager,
    private val dynamicBitrateController: DynamicBitrateController,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val networkStateProvider: INetworkStateProvider,
    private val streamingPipeline: IStreamingPipeline,
    private val mediaSessionProvider: IMediaSessionProvider,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    override var activeMediaId: String? = null
        private set

    private val attachedPlayers = WeakHashMap<Player, Boolean>()
    private val decoderFailures = mutableSetOf<String>()
    private val composedMediaIds = mutableSetOf<String>()
    private var wasPlayingBeforePause: Boolean = false
    
    private var mediaSession: MediaSession? = null

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

    override suspend fun play(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        title: String?,
        artist: String?
    ) =
        withContext(playerDispatcher) {
            checkConfinement()
            
            // 🌡️ Internal Decoder Fallback:
            // The engine now automatically handles the decision to force legacy if previous attempts failed.
            val forceLegacy = decoderFailures.contains(mediaId)

            val managed = pool.getOrCreate(mediaId, uri, mediaType, forceLegacy, title, artist)
            val environment = environmentCoordinator.environment.value

            if (activeMediaId != mediaId) {
                // 🏎️ Authoritative Source Check:
                // Ensure we pause any previous player correctly using the engine's pointer.
                activeMediaId?.let { previous -> pool.get(previous)?.player?.pause() }
            }

            attachListenerIfNeeded(managed)
            managed.analyticsListener.markPlaybackStart()
            dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
            
            // 🎙️ System Media Session Update
            updateMediaSession(managed.player)
            
            managed.player.playWhenReady = true
            managed.player.prepare()
            managed.player.play()
            
            audioFocusManager.request()
            
            activeMediaId = mediaId
            environmentManager.updateActiveMediaId(mediaId)
            
            streamingPipeline.warm(mediaId, uri, WarmPriority.VISIBLE)
        }

    @OptIn(UnstableApi::class)
    override suspend fun preload(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        title: String?,
        artist: String?
    ): ManagedPlayer? =
        withContext(playerDispatcher) {
            checkConfinement()
            // Speculative preloads don't force legacy until they fail once
            val result = pool.prewarm(mediaId, uri, mediaType, false, false, title, artist)
            if (result is PrewarmResult.Success) {
                val managed = result.managed
                val environment = environmentCoordinator.environment.value
                dynamicBitrateController.apply(managed.player, mediaType, environment, startupPhase = true)
                attachListenerIfNeeded(managed)
                managed
            } else null
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
            audioFocusManager.cleanup()
            mediaSession?.release()
            mediaSession = null
            pool.releaseAll()
            environmentManager.stop()
        }
    }

    override fun isPlaying(): Boolean {
        checkConfinement()
        return isCurrentlyPlaying()
    }

    override fun isMediaActive(mediaId: String): Boolean {
        checkConfinement()
        return activeMediaId == mediaId
    }

    override fun notifyMediaBound(mediaId: String) {
        checkConfinement()
        composedMediaIds.add(mediaId)
        environmentManager.updatePinnedIds(composedMediaIds)
        pool.updatePinnedIds(composedMediaIds)
    }

    override fun notifyMediaUnbound(mediaId: String) {
        checkConfinement()
        composedMediaIds.remove(mediaId)
        environmentManager.updatePinnedIds(composedMediaIds)
        pool.updatePinnedIds(composedMediaIds)
    }

    private fun updateMediaSession(player: ExoPlayer) {
        if (mediaSession == null) {
            mediaSession = mediaSessionProvider.create(player)
        } else {
            mediaSession?.player = player
        }
    }

    private fun pauseCurrentPlayer() {
        activeMediaId?.let { pool.get(it)?.player?.pause() }
    }

    private fun isCurrentlyPlaying(): Boolean {
        checkConfinement()
        return activeMediaId?.let { pool.get(it)?.player?.isPlaying } ?: false
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("PlayerManager must only be accessed from the Main thread.")
        }
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
        if (attachedPlayers.containsKey(managed.player)) return
        attachedPlayers[managed.player] = true

        managed.player.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                val currentManaged = resolveManaged(player) ?: return
                
                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                    when (player.playbackState) {
                        Player.STATE_IDLE -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.Reset)
                        Player.STATE_BUFFERING -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
                        Player.STATE_READY -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
                        Player.STATE_ENDED -> currentManaged.reducer.dispatch(PlaybackStateReducer.Event.PlaybackEnded)
                    }
                }

                if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    currentManaged.reducer.dispatch(
                        if (player.isPlaying) PlaybackStateReducer.Event.Play else PlaybackStateReducer.Event.Pause
                    )
                }

                if (events.contains(Player.EVENT_PLAYER_ERROR)) {
                    player.playerError?.let { error ->
                        engineScope.launch {
                            // 🌡️ Handle Decoder Failure internally
                            if (error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED) {
                                pool.getMediaId(player as ExoPlayer)?.let { decoderFailures.add(it) }
                            }

                            val isNetworkError = isNetworkError(error)
                            val networkState = networkStateProvider.current()
                            
                            if (isNetworkError && networkState !is NetworkState.Connected) {
                                currentManaged.reducer.dispatch(PlaybackStateReducer.Event.NetworkLost)
                            } else {
                                currentManaged.reducer.dispatch(PlaybackStateReducer.Event.PlaybackError(error))
                            }
                        }
                    }
                }
                
                if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME)) {
                    val bufferSeconds = (player.bufferedPosition - player.currentPosition) / 1000.0
                    dynamicBitrateController.apply(
                        player as ExoPlayer,
                        currentManaged.mediaType,
                        environmentCoordinator.environment.value,
                        bufferSeconds = bufferSeconds,
                        startupPhase = false
                    )
                }
            }

            private fun resolveManaged(player: Player): ManagedPlayer? {
                val mediaId = pool.getMediaId(player as ExoPlayer) ?: return null
                return pool.get(mediaId)
            }
        })
    }

    // region Testing Hooks
    val debugActiveMediaId: String?
        get() = activeMediaId
    // endregion
}
