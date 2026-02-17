package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.common.interfaces.IBatteryManager
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
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
    private val contentPreloader: ContentPreloader,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val playerFactory: PlayerFactory,
    private val engineScope: CoroutineScope,
    private val dynamicBitrateController: DynamicBitrateController,
    private val networkUtils: INetworkUtils,
    private val batteryManager: IBatteryManager,
    private val playerDispatcher: CoroutineDispatcher,
    private val maxPoolSize: Int = 4
) : IPlayer {

    // ------------------------------------------------------------
    // Holder for player + mediaType
    // ------------------------------------------------------------
    private data class PlayerHolder(
        val player: ExoPlayer,
        val mediaType: MediaType
    )

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, PlayerHolder>()
    private val stateMachines = mutableMapOf<ExoPlayer, PlayerStateMachine>()

    private var currentHolder: PlayerHolder? = null
    private var currentView: PlayerView? = null
    private var currentKey: String? = null

    private var lastEnvironment: Pair<Any, Any>? = null // replace Any with your actual types

    init {
        observeEnvironment()
    }

    // ------------------------------------------------------------
    // Global Environment Observer
    // ------------------------------------------------------------
    private fun observeEnvironment() {
        engineScope.launch(playerDispatcher) {
            combine(
                networkUtils.observeNetworkStatus(),
                batteryManager.observeBatteryState()
            ) { network, battery ->
                network to battery
            }.collect { (network, battery) ->

                lastEnvironment = network to battery

                activePlayers.values.forEach { holder ->
                    dynamicBitrateController.onEnvironmentChanged(
                        holder.player,
                        holder.mediaType
                    )
                }
            }
        }
    }

    // ------------------------------------------------------------
    // Acquire Player
    // ------------------------------------------------------------
    override suspend fun acquirePlayer(
        mediaId: String,
        mediaType: MediaType
    ): ExoPlayer = withContext(playerDispatcher) {
        acquirePlayerInternal(mediaId, mediaType).player
    }

    private fun acquirePlayerInternal(
        mediaId: String,
        mediaType: MediaType
    ): PlayerHolder {

        activePlayers[mediaId]?.let { return it }

        val reusable = playerPool.firstOrNull {
            it !in activePlayers.values.map { holder -> holder.player } && !it.isPlaying
        }

        if (reusable == null && playerPool.size >= maxPoolSize) {
            evictLeastRecentlyUsed()
        }

        val strategy = mapMediaTypeToStrategy(mediaType)

        val player = reusable ?: createNewPlayer(strategy, mediaType)

        val holder = PlayerHolder(player, mediaType)
        activePlayers[mediaId] = holder

        // Apply latest environment immediately if available
        lastEnvironment?.let {
            dynamicBitrateController.onEnvironmentChanged(player, mediaType)
        }

        return holder
    }

    // ------------------------------------------------------------
    // Release Player
    // ------------------------------------------------------------
    override suspend fun releasePlayer(mediaId: String) =
        withContext(playerDispatcher) {

            val holder = activePlayers.remove(mediaId) ?: return@withContext
            val player = holder.player

            if (currentHolder?.player == player) {
                detachInternal()
            }

            stateMachines.remove(player)
            playerPool.remove(player)

            player.stop()
            player.clearMediaItems()
            player.release()
        }

    // ------------------------------------------------------------
    // Attach Player
    // ------------------------------------------------------------
    override suspend fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        detachInternal()

        val holder = acquirePlayerInternal(mediaId, mediaType)
        val player = holder.player

        if (isValidMediaUrl(mediaId)) {
            val strategy = mapMediaTypeToStrategy(mediaType)
            val mediaItem = strategy.createMediaItem(mediaId)

            playbackAnalyticsListener.markPlaybackStart()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }

        currentHolder = holder
        currentView = playerView
        currentKey = mediaId

        playerView.player = player
    }

    // ------------------------------------------------------------
    // Detach
    // ------------------------------------------------------------
    override suspend fun detachPlayer() =
        withContext(playerDispatcher) {
            detachInternal()
        }

    private fun detachInternal() {
        currentView?.player = null
        currentView = null
        currentHolder?.player?.pause()
        currentHolder = null
        currentKey = null
    }

    // ------------------------------------------------------------
    // Playback Controls
    // ------------------------------------------------------------
    override suspend fun resume() {
        withContext(playerDispatcher) {
            currentHolder?.player?.play()
        }
    }

    override suspend fun pause() {
        withContext(playerDispatcher) {
            currentHolder?.player?.pause()
        }
    }

    override suspend fun getCurrentPlayer(): ExoPlayer? =
        withContext(playerDispatcher) {
            currentHolder?.player
        }

    // ------------------------------------------------------------
    // Preloading
    // ------------------------------------------------------------
    override suspend fun preloadMedia(mediaId: String) =
        withContext(playerDispatcher) {
            contentPreloader.schedulePreload(mediaId)
        }

    // ------------------------------------------------------------
    // Observe State
    // ------------------------------------------------------------
    fun observePlayerState(player: ExoPlayer): StateFlow<PlayerStateMachine.State>? {
        return stateMachines[player]?.state
    }

    // ------------------------------------------------------------
    // Player Creation
    // ------------------------------------------------------------
    private fun createNewPlayer(
        strategy: PlayerConfigurationStrategy,
        mediaType: MediaType
    ): ExoPlayer {

        val player = playerFactory.create(strategy)
        playerPool.add(player)

        val stateMachine = PlayerStateMachine()
        stateMachines[player] = stateMachine

        attachListener(player, stateMachine)

        // Initial ABR attach
        dynamicBitrateController.attach(player, mediaType)

        return player
    }

    private fun evictLeastRecentlyUsed() {
        val activeSet = activePlayers.values.map { it.player }.toSet()

        val lru = playerPool.firstOrNull { it !in activeSet } ?: return

        stateMachines.remove(lru)
        playerPool.remove(lru)

        lru.stop()
        lru.clearMediaItems()
        lru.release()
    }

    private fun attachListener(
        player: ExoPlayer,
        stateMachine: PlayerStateMachine
    ) {
        player.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE ->
                        stateMachine.transition(PlayerStateMachine.Event.Reset)
                    Player.STATE_BUFFERING ->
                        stateMachine.transition(PlayerStateMachine.Event.BufferingStarted)
                    Player.STATE_READY ->
                        stateMachine.transition(PlayerStateMachine.Event.BufferingCompleted)
                    Player.STATE_ENDED ->
                        stateMachine.transition(PlayerStateMachine.Event.PlaybackEnded)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    stateMachine.transition(PlayerStateMachine.Event.Play)
                } else {
                    stateMachine.transition(PlayerStateMachine.Event.Pause)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                stateMachine.transition(
                    PlayerStateMachine.Event.PlaybackError(error)
                )
            }
        })
    }

    private fun isValidMediaUrl(value: String): Boolean =
        value.isNotBlank() && (
                value.startsWith("http://", true) ||
                        value.startsWith("https://", true) ||
                        value.startsWith("content://") ||
                        value.startsWith("file://")
                )

    private fun mapMediaTypeToStrategy(
        mediaType: MediaType
    ): PlayerConfigurationStrategy =
        when (mediaType) {
            MediaType.LIVE -> playerFactory.liveStrategy
            MediaType.VOD -> playerFactory.vodStrategy
        }
}
