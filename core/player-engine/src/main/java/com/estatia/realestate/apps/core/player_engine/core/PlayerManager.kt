package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val sizingPolicy: IPlayerPoolSizingPolicy,
    private val dynamicBitrateController: DynamicBitrateController,
    engineScope: CoroutineScope,
    private val playerDispatcher: CoroutineDispatcher
) : IPlayerManager {

    private val reducer = PlaybackStateReducer()

    private var activeMediaId: String? = null

    init {
        require(playerDispatcher is ExecutorCoroutineDispatcher) {
            "PlayerDispatcher must be single-threaded"
        }

        // Start environment monitoring
        environmentCoordinator.start(engineScope)

        // React to environment changes
        engineScope.launch(playerDispatcher) {
            environmentCoordinator.environment.collect { env ->

                // 1️⃣ Update adaptive pool size
                val newSize = sizingPolicy.calculateMaxPoolSize()
                pool.updateMaxPoolSize(newSize, activeMediaId)

                // 2️⃣ Apply environment to all pooled players
                pool.forEachPlayer { player, mediaType ->
                    dynamicBitrateController.apply(
                        player,
                        mediaType,
                        env
                    )
                }
            }
        }
    }

    override suspend fun play(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        val managed = pool.getOrCreate(mediaId, mediaType)

        if (activeMediaId != mediaId) {
            activeMediaId?.let { previous ->
                pool.get(previous)?.player?.pause()
            }
        }

        attachListenerIfNeeded(managed.player)

        managed.player.play()
        activeMediaId = mediaId
    }

    override suspend fun preload(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {
        pool.getOrCreate(mediaId, mediaType)
    }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            activeMediaId?.let { pool.get(it)?.player?.pause() }
        }
    }

    override suspend fun getPlayer(
        mediaId: String,
        mediaType: MediaType
    ): Player = withContext(playerDispatcher) {
        pool.getOrCreate(mediaId, mediaType).player
    }

    override fun observeState(): StateFlow<PlaybackStateReducer.State> =
        reducer.state

    override fun shutdown() {
        pool.releaseAll()
        (playerDispatcher as ExecutorCoroutineDispatcher).close()
    }

    // ---------------------------------------------------
    // Listener Management
    // ---------------------------------------------------

    private val attachedPlayers = mutableSetOf<ExoPlayer>()

    private fun attachListenerIfNeeded(player: ExoPlayer) {
        if (!attachedPlayers.add(player)) return

        player.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE ->
                        reducer.dispatch(PlaybackStateReducer.Event.Reset)

                    Player.STATE_BUFFERING ->
                        reducer.dispatch(
                            PlaybackStateReducer.Event.BufferingStarted
                        )

                    Player.STATE_READY ->
                        reducer.dispatch(
                            PlaybackStateReducer.Event.BufferingCompleted
                        )

                    Player.STATE_ENDED ->
                        reducer.dispatch(
                            PlaybackStateReducer.Event.PlaybackEnded
                        )
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                reducer.dispatch(
                    if (isPlaying)
                        PlaybackStateReducer.Event.Play
                    else
                        PlaybackStateReducer.Event.Pause
                )
            }

            override fun onPlayerError(error: PlaybackException) {
                reducer.dispatch(
                    PlaybackStateReducer.Event.PlaybackError(error)
                )
            }
        })
    }
}