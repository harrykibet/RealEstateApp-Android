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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val playbackState = PlaybackState()

    // ---------------------------------------
    // Player Pool (max 3)
    // ---------------------------------------

    private val playerPool = LinkedHashMap<String, ExoPlayer>()
    private val maxPoolSize = 3

    private var activeMediaId: String? = null
    private var activeMediaType: MediaType? = null

    init {
        observeEnvironment()
    }

    // ---------------------------------------
    // Environment Observer
    // ---------------------------------------

    private fun observeEnvironment() {
        engineScope.launch(playerDispatcher) {
            combine(
                networkUtils.observeNetworkStatus(),
                batteryManager.observeBatteryState()
            ) { network, battery ->
                network to battery
            }.collect {
                activeMediaId?.let { mediaId ->
                    playerPool[mediaId]?.let { player ->
                        activeMediaType?.let { type ->
                            dynamicBitrateController.onEnvironmentChanged(player, type)
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------
    // Play
    // ---------------------------------------

    override suspend fun play(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        val player = getOrCreatePlayer(mediaId, mediaType)

        // Pause currently active player
        activeMediaId?.let { currentId ->
            if (currentId != mediaId) {
                playerPool[currentId]?.pause()
            }
        }

        player.play()
        activeMediaId = mediaId
        activeMediaType = mediaType

        dynamicBitrateController.attach(player, mediaType)
    }

    // ---------------------------------------
    // Preload (prepare only)
    // ---------------------------------------

    override suspend fun preload(
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        if (playerPool.containsKey(mediaId)) return@withContext

        getOrCreatePlayer(mediaId, mediaType)
    }

    // ---------------------------------------
    // Pause Active
    // ---------------------------------------

    override suspend fun pause() {
        withContext(playerDispatcher) {
            activeMediaId?.let {
                playerPool[it]?.pause()
            }
        }
    }

    // ---------------------------------------
    // Get Player for Surface Attachment
    // ---------------------------------------

    override suspend fun getPlayer(
        mediaId: String
    ): Player = withContext(playerDispatcher) {

        playerPool[mediaId]
            ?: getOrCreatePlayer(mediaId, activeMediaType ?: MediaType.VOD)
    }

    // ---------------------------------------
    // Observe Playback State
    // ---------------------------------------

    override fun observeState(): StateFlow<PlaybackState.State> =
        playbackState.state

    // ---------------------------------------
    // Internal: Player Creation
    // ---------------------------------------

    private fun getOrCreatePlayer(
        mediaId: String,
        mediaType: MediaType
    ): ExoPlayer {

        playerPool[mediaId]?.let { return it }

        val strategy = when (mediaType) {
            MediaType.LIVE -> playerFactory.liveStrategy
            MediaType.VOD -> playerFactory.vodStrategy
        }

        val player = playerFactory.create(strategy).apply {
            val mediaItem = strategy.createMediaItem(mediaId)
            setMediaItem(mediaItem)
            prepare()
            addAnalyticsListener(playbackAnalyticsListener)
            attachListener(this)
        }

        playerPool[mediaId] = player
        trimPoolIfNeeded()

        return player
    }

    // ---------------------------------------
    // Eviction Strategy (LRU)
    // ---------------------------------------

    private fun trimPoolIfNeeded() {
        if (playerPool.size <= maxPoolSize) return

        val iterator = playerPool.entries.iterator()
        while (iterator.hasNext() && playerPool.size > maxPoolSize) {
            val entry = iterator.next()

            // Do not remove active player
            if (entry.key == activeMediaId) continue

            entry.value.release()
            iterator.remove()
        }
    }

    // ---------------------------------------
    // Attach Player Listener
    // ---------------------------------------

    private fun attachListener(player: ExoPlayer) {
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
