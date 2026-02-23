package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val pool: PlayerPool,
    private val environmentCoordinator: EnvironmentCoordinator,
    engineScope: CoroutineScope,
    private val playerDispatcher: CoroutineDispatcher
) : ISharedPlayerController {

    private val reducer = PlaybackStateReducer()

    private var activeMediaId: String? = null

    init {
        require(playerDispatcher is ExecutorCoroutineDispatcher) {
            "PlayerDispatcher must be single-threaded"
        }

        environmentCoordinator.observe(engineScope) {
            activeMediaId?.let { id ->
                pool.get(id)?.let {
                    it.player to it.mediaType
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
                pool.get(previous)?.player?.let {
                    environmentCoordinator.detach(it)
                }
            }
        }

        managed.player.play()
        attachListener(managed.player)

        environmentCoordinator.attach(managed.player, mediaType)

        activeMediaId = mediaId

        pool.trimIfNeeded(excludeMediaId = mediaId)
    }

    override suspend fun preload(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {
        pool.getOrCreate(mediaId, mediaType)
    }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            activeMediaId?.let {
                pool.get(it)?.player?.pause()
            }
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

    private fun attachListener(player: ExoPlayer) {
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