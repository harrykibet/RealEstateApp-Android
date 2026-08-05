package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.PlayerDispatcher
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

// PlayerManager.kt
@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val dynamicBitrateController: DynamicBitrateController,
    @param:EngineScope private val engineScope: CoroutineScope,
    @param:PlayerDispatcher private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    private var activeMediaId: String? = null
    private val activeMediaIdFlow = MutableStateFlow<String?>(null)
    private val attachedPlayers = mutableSetOf<ExoPlayer>()

    init {
        environmentCoordinator.start(engineScope)
        engineScope.launch(playerDispatcher) {
            environmentCoordinator.environment.collect { env ->
                val newSize = sizingPolicy.calculateMaxPoolSize()
                pool.updateMaxPoolSize(newSize, activeMediaId)
                pool.forEachPlayer { player, mediaType ->
                    dynamicBitrateController.apply(player, mediaType, env)
                }
            }
        }
    }

    override suspend fun play(mediaId: String, mediaType: MediaType) =
        withContext(playerDispatcher) {
            val managed = pool.getOrCreate(mediaId, mediaType)

            if (activeMediaId != mediaId) {
                activeMediaId?.let { previous -> pool.get(previous)?.player?.pause() }
            }

            attachListenerIfNeeded(managed)
            managed.analyticsListener.markPlaybackStart()

            managed.player.play()
            activeMediaId = mediaId
            activeMediaIdFlow.value = mediaId
        }

    override suspend fun preload(mediaId: String, mediaType: MediaType) =
        withContext(playerDispatcher) {
            val managed = pool.getOrCreate(mediaId, mediaType)
            attachListenerIfNeeded(managed)
            managed
        }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            activeMediaId?.let { pool.get(it)?.player?.pause() }
        }
    }

    override suspend fun getPlayer(mediaId: String, mediaType: MediaType): Player =
        withContext(playerDispatcher) { pool.getOrCreate(mediaId, mediaType).player }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeState(): StateFlow<PlaybackStateReducer.State> =
        activeMediaIdFlow
            .flatMapLatest { mediaId ->
                mediaId?.let { id -> pool.get(id)?.reducer?.state }
                    ?: MutableStateFlow(PlaybackStateReducer.State.Idle)
            }
            .flowOn(playerDispatcher)
            .stateIn(engineScope, SharingStarted.Eagerly, PlaybackStateReducer.State.Idle)

    override fun shutdown() {
        pool.releaseAll()
        // No need to close Dispatchers.Main
    }

    private fun attachListenerIfNeeded(managed: PlayerPool.ManagedPlayer) {
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
                managed.reducer.dispatch(PlaybackStateReducer.Event.PlaybackError(error))
            }
        })
    }
}
