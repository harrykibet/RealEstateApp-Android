package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.strategies.PlayerConfigurationStrategy
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class PlayerManager @Inject constructor(
    private val contentPreloader: ContentPreloader,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val playerFactory: PlayerFactory,
    private val engineScope: CoroutineScope,          // long-lived engineScope
    private val playerDispatcher: CoroutineDispatcher, // single-threaded dispatcher
    private val maxPoolSize: Int = 4
) : IPlayer {

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()
    private val playerJobs = mutableMapOf<ExoPlayer, Job>()

    @Volatile private var _currentPlayer: ExoPlayer? = null
    @Volatile private var _currentView: PlayerView? = null
    @Volatile private var _currentKey: String? = null

    // ------------------------
    // Acquire player (LRU eviction + reuse)
    // ------------------------
    override suspend fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer {
        var player: ExoPlayer? = null

        val job = engineScope.launch(playerDispatcher) {
            // Already active?
            activePlayers[mediaId]?.let {
                player = it
                return@launch
            }

            // Reusable player
            val reusable = playerPool.firstOrNull { it !in activePlayers.values && !it.isPlaying }

            // Evict LRU if needed
            if (reusable == null && playerPool.size >= maxPoolSize) {
                val lru = playerPool.firstOrNull { it !in activePlayers.values }
                lru?.let {
                    it.stop()
                    it.clearMediaItems()
                    it.release()
                    playerPool.remove(it)
                }
            }

            val strategy = mapMediaTypeToStrategy(mediaType)
            player = reusable ?: playerFactory.create(strategy).also { playerPool.add(it) }
            activePlayers[mediaId] = player
        }

        job.join()
        return player!!
    }

    // ------------------------
    // Release player
    // ------------------------
    override fun releasePlayer(mediaId: String) {
        engineScope.launch(playerDispatcher) {
            val player = activePlayers.remove(mediaId) ?: return@launch
            playerJobs.remove(player)?.cancel()

            if (_currentPlayer == player) {
                _currentView?.player = null
                _currentView = null
                _currentPlayer = null
                _currentKey = null
            }

            player.stop()
            player.clearMediaItems()
            player.release()
        }
    }

    // ------------------------
    // Attach / detach
    // ------------------------
    override suspend fun attachPlayerToView(playerView: PlayerView, mediaId: String, mediaType: MediaType) {
        engineScope.launch(playerDispatcher) {
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

            _currentPlayer = player
            _currentView = playerView
            _currentKey = mediaId
            playerView.player = player
        }
    }

    override fun detachPlayer() {
        engineScope.launch(playerDispatcher) {
            _currentView?.player = null
            _currentView = null
            _currentPlayer?.pause()
            _currentPlayer = null
            _currentKey = null
        }
    }

    override fun preloadMedia(mediaId: String) {
        engineScope.launch(playerDispatcher) {
            contentPreloader.schedulePreload(mediaId)
        }
    }

    override suspend fun resume() {
        engineScope.launch(playerDispatcher) {
            _currentPlayer?.play()
        }
    }

    override suspend fun pause() {
        engineScope.launch(playerDispatcher) {
            _currentPlayer?.pause()
        }
    }

    override fun getCurrentPlayer(): ExoPlayer? = _currentPlayer

    // ------------------------
    // Helpers
    // ------------------------
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
