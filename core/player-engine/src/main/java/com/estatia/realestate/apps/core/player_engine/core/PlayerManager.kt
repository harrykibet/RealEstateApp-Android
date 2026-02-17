package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val contentPreloader: ContentPreloader,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val playerFactory: PlayerFactory,
    private val playerDispatcher: CoroutineDispatcher,     // single-threaded dispatcher
    private val maxPoolSize: Int = 4
) : IPlayer {

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()
    private val stateMachines = mutableMapOf<ExoPlayer, PlayerStateMachine>()

    private var currentPlayer: ExoPlayer? = null
    private var currentView: PlayerView? = null
    private var currentKey: String? = null

    // ----------------------------------------------------------------
    // Acquire Player (Single-thread safe via dispatcher serialization)
    // ----------------------------------------------------------------
    override suspend fun acquirePlayer(
        mediaId: String,
        mediaType: MediaType
    ): ExoPlayer = withContext(playerDispatcher) {

        activePlayers[mediaId]?.let { return@withContext it }

        val reusable = playerPool.firstOrNull {
            it !in activePlayers.values && !it.isPlaying
        }

        if (reusable == null && playerPool.size >= maxPoolSize) {
            evictLeastRecentlyUsed()
        }

        val strategy = mapMediaTypeToStrategy(mediaType)

        val player = reusable ?: createNewPlayer(strategy)

        activePlayers[mediaId] = player
        player
    }

    // ----------------------------------------------------------------
    // Release Player
    // ----------------------------------------------------------------
    override suspend fun releasePlayer(mediaId: String) =
        withContext(playerDispatcher) {

            val player = activePlayers.remove(mediaId) ?: return@withContext

            if (currentPlayer == player) {
                detachInternal()
            }

            stateMachines.remove(player)
            playerPool.remove(player)

            player.stop()
            player.clearMediaItems()
            player.release()
        }

    // ----------------------------------------------------------------
    // Attach Player
    // ----------------------------------------------------------------
    override suspend fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    ) = withContext(playerDispatcher) {

        detachInternal()

        val player = acquirePlayer(mediaId, mediaType)

        if (isValidMediaUrl(mediaId)) {
            val strategy = mapMediaTypeToStrategy(mediaType)
            val mediaItem = strategy.createMediaItem(mediaId)

            playbackAnalyticsListener.markPlaybackStart()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }

        currentPlayer = player
        currentView = playerView
        currentKey = mediaId

        playerView.player = player
    }

    // ----------------------------------------------------------------
    // Detach
    // ----------------------------------------------------------------
    override suspend fun detachPlayer() =
        withContext(playerDispatcher) {
            detachInternal()
        }

    private fun detachInternal() {
        currentView?.player = null
        currentView = null
        currentPlayer?.pause()
        currentPlayer = null
        currentKey = null
    }

    // ----------------------------------------------------------------
    // Playback Controls
    // ----------------------------------------------------------------
    override suspend fun resume(): Unit =
        withContext(playerDispatcher) {
            currentPlayer?.play()
        }

    override suspend fun pause(): Unit =
        withContext(playerDispatcher) {
            currentPlayer?.pause()
        }

    override fun getCurrentPlayer(): ExoPlayer? = currentPlayer

    // ----------------------------------------------------------------
    // Preloading
    // ----------------------------------------------------------------
    override suspend fun preloadMedia(mediaId: String) =
        withContext(playerDispatcher) {
            contentPreloader.schedulePreload(mediaId)
        }

    // ----------------------------------------------------------------
    // Observe State
    // ----------------------------------------------------------------
    fun observePlayerState(player: ExoPlayer): StateFlow<PlayerStateMachine.State>? {
        return stateMachines[player]?.state
    }

    // ----------------------------------------------------------------
    // Internal Helpers
    // ----------------------------------------------------------------
    private fun createNewPlayer(
        strategy: PlayerConfigurationStrategy
    ): ExoPlayer {

        val player = playerFactory.create(strategy)
        playerPool.add(player)

        val stateMachine = PlayerStateMachine()
        stateMachines[player] = stateMachine

        attachListener(player, stateMachine)

        return player
    }

    private fun evictLeastRecentlyUsed() {
        val lru = playerPool.firstOrNull { it !in activePlayers.values }
            ?: return

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
