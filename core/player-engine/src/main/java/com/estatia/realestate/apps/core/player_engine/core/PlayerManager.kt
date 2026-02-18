package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.utils.DynamicBitrateController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val playerFactory: PlayerFactory,
    private val dynamicBitrateController: DynamicBitrateController,
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val engineScope: CoroutineScope,
    private val playerDispatcher: CoroutineDispatcher
) : ISharedPlayerController {

    // ------------------------------------------------------------
    // Core shared state
    // ------------------------------------------------------------

    private val player: ExoPlayer =
        playerFactory.create(playerFactory.vodStrategy).apply {
            addAnalyticsListener(playbackAnalyticsListener)
        }

    private var currentMediaId: String? = null
    private var currentMediaType: MediaType? = null

    private val playbackState = PlaybackState()

    init {
        attachPlayerListener()
        observeEnvironment()
    }

    // ------------------------------------------------------------
    // Environment Observer (GLOBAL, SAFE)
    // ------------------------------------------------------------

    private fun observeEnvironment() {
        engineScope.launch(playerDispatcher) {
            combine(
                networkUtils.observeNetworkStatus(),
                batteryManager.observeBatteryState()
            ) { network, battery ->
                network to battery
            }.collect {
                currentMediaType?.let { type ->
                    dynamicBitrateController.onEnvironmentChanged(player, type)
                }
            }
        }
    }

    // ------------------------------------------------------------
    // Play / Switch Media
    // ------------------------------------------------------------

    override suspend fun play(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        if (mediaId == currentMediaId) {
            player.play()
            return@withContext
        }

        val strategy = when (mediaType) {
            MediaType.LIVE -> playerFactory.liveStrategy
            MediaType.VOD -> playerFactory.vodStrategy
        }

        val mediaItem = strategy.createMediaItem(mediaId)

        playbackAnalyticsListener.markPlaybackStart()

        player.apply {
            stop()
            clearMediaItems()
            setMediaItem(mediaItem)
            prepare()
            play()
        }

        currentMediaId = mediaId
        currentMediaType = mediaType

        dynamicBitrateController.attach(player, mediaType)
    }

    // ------------------------------------------------------------
    // Pause (GLOBAL)
    // ------------------------------------------------------------

    override suspend fun pause() =
        withContext(playerDispatcher) {
            player.pause()
        }

    // ------------------------------------------------------------
    // Preload (Adjacent Items)
    // ------------------------------------------------------------

    override suspend fun preload(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        if (mediaId == currentMediaId) return@withContext

        val strategy = when (mediaType) {
            MediaType.LIVE -> playerFactory.liveStrategy
            MediaType.VOD -> playerFactory.vodStrategy
        }

        player.addMediaItem(strategy.createMediaItem(mediaId))
        player.prepare()
    }

    // ------------------------------------------------------------
    // Observe Playback State (GLOBAL)
    // ------------------------------------------------------------

    override fun observeState(): StateFlow<PlaybackState.State> =
        playbackState.state

    // ------------------------------------------------------------
    // Surface Attachment
    // ------------------------------------------------------------

    override suspend fun getPlayer(): Player =
        withContext(playerDispatcher) {
            player
        }

    // ------------------------------------------------------------
    // Internal Listener
    // ------------------------------------------------------------

    private fun attachPlayerListener() {
        player.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE ->
                        playbackState.transition(PlaybackState.Event.Reset)

                    Player.STATE_BUFFERING ->
                        playbackState.transition(PlaybackState.Event.BufferingStarted)

                    Player.STATE_READY ->
                        playbackState.transition(PlaybackState.Event.BufferingCompleted)

                    Player.STATE_ENDED ->
                        playbackState.transition(PlaybackState.Event.PlaybackEnded)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playbackState.transition(PlaybackState.Event.Play)
                } else {
                    playbackState.transition(PlaybackState.Event.Pause)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                playbackState.transition(
                    PlaybackState.Event.PlaybackError(error)
                )
            }
        })
    }
}
