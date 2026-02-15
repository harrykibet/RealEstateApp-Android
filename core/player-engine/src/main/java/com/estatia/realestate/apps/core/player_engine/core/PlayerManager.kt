package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Player instances for feed-style playback.
 * Feature modules just specify MediaType; strategy details are hidden.
 */


@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val contentPreloader: ContentPreloader,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val playerFactory: PlayerFactory,
    private val maxPoolSize: Int = 4 // configurable max number of players in memory
) : IPlayer {

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    private val lock = Any()

    @Volatile private var _currentPlayer: ExoPlayer? = null
    @Volatile private var _currentView: PlayerView? = null
    @Volatile private var _currentKey: String? = null

    private val currentPlayer: ExoPlayer? get() = synchronized(lock) { _currentPlayer }

    // ------------------------------------
    // Acquire Player with LRU Eviction
    // ------------------------------------
    override fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer {
        synchronized(lock) {
            activePlayers[mediaId]?.let { return it }

            // Try to find a reusable player not in activePlayers
            val reusable = playerPool.firstOrNull { player ->
                !player.isPlaying && !activePlayers.containsValue(player)
            }

            // Evict least recently used player if pool is full
            if (reusable == null && playerPool.size >= maxPoolSize) {
                val lruPlayer = playerPool.firstOrNull { !activePlayers.containsValue(it) }
                lruPlayer?.let {
                    it.stop()
                    it.clearMediaItems()
                    it.release()
                    playerPool.remove(it)
                }
            }

            val strategy = mapMediaTypeToStrategy(mediaType)

            val player = reusable ?: playerFactory.create(strategy).also {
                playerPool.add(it)
            }

            activePlayers[mediaId] = player
            return player
        }
    }

    // ------------------------------------
    // Release Player
    // ------------------------------------
    override fun releasePlayer(mediaId: String) {
        synchronized(lock) {
            val player = activePlayers.remove(mediaId) ?: return
            if (_currentPlayer == player) {
                _currentView?.player = null
                _currentView = null
                _currentPlayer = null
                _currentKey = null
            }
            player.stop()
            player.clearMediaItems()
        }
    }

    // ------------------------------------
    // Attach/Detach Player
    // ------------------------------------
    override fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    ) {
        synchronized(lock) {
            detachPlayer()

            val player = acquirePlayer(mediaId, mediaType)

            if (isValidMediaUrl(mediaId)) {
                val strategy = mapMediaTypeToStrategy(mediaType)
                val mediaItem = strategy.createMediaItem(mediaId)

                playbackAnalyticsListener.markPlaybackStart()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
            }

            playerView.player = player
            _currentPlayer = player
            _currentView = playerView
            _currentKey = mediaId
        }
    }

    override fun detachPlayer() {
        synchronized(lock) {
            _currentView?.player = null
            _currentView = null
            _currentPlayer?.pause()
            _currentPlayer = null
            _currentKey = null
        }
    }

    override fun preloadMedia(mediaId: String) {
        contentPreloader.schedulePreload(mediaId)
    }

    override fun resume() { synchronized(lock) { _currentPlayer?.play() } }
    override fun pause() { synchronized(lock) { _currentPlayer?.pause() } }
    override fun getCurrentPlayer(): ExoPlayer? = currentPlayer

    private fun isValidMediaUrl(value: String): Boolean =
        value.isNotBlank() && (
                value.startsWith("http://", true) ||
                        value.startsWith("https://", true) ||
                        value.startsWith("content://") ||
                        value.startsWith("file://")
                )

    private fun mapMediaTypeToStrategy(mediaType: MediaType): PlayerConfigurationStrategy =
        when (mediaType) {
            MediaType.LIVE -> playerFactory.liveStrategy
            MediaType.VOD -> playerFactory.vodStrategy
        }
}


