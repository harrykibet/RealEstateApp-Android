package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
import javax.inject.Inject
import javax.inject.Singleton



/**
 * Manages ExoPlayer instances for feed-style playback.
 * Feature modules just specify MediaType; strategy details are hidden.
 */
@UnstableApi
@Singleton
class ExoPlayerInstanceManager @Inject constructor(
    private val contentPreloader: ContentPreloader,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    private val exoPlayerFactory: ExoPlayerFactory
) : IPlayer {

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    @Volatile private var currentPlayer: ExoPlayer? = null
    @Volatile private var currentView: PlayerView? = null
    @Volatile private var currentKey: String? = null

    // ------------------------------------
    // Player Pool
    // ------------------------------------

    override fun acquirePlayer(mediaId: String, mediaType: MediaType): ExoPlayer {
        activePlayers[mediaId]?.let { return it }

        val reusable = playerPool.firstOrNull { !it.isPlaying }

        val strategy = mapMediaTypeToStrategy(mediaType)

        val player = reusable?.also {
            it.stop()
            it.clearMediaItems()
        } ?: exoPlayerFactory.create(strategy).also {
            playerPool.add(it)
        }

        activePlayers[mediaId] = player
        return player
    }

    override fun releasePlayer(mediaId: String) {
        val player = activePlayers.remove(mediaId) ?: return
        if (currentPlayer == player) {
            currentView?.player = null
            currentView = null
            currentPlayer = null
            currentKey = null
        }
        player.stop()
        player.clearMediaItems()
    }

    // ------------------------------------
    // Media Handling
    // ------------------------------------

    override fun attachPlayerToView(
        playerView: PlayerView,
        mediaId: String,
        mediaType: MediaType
    ) {
        detachPlayer()

        val player = acquirePlayer(mediaId, mediaType)

        if (isValidMediaUrl(mediaId)) {
            val strategy = mapMediaTypeToStrategy(mediaType)
            val mediaItem = strategy.createMediaItem(mediaId)

            // Mark playback start before prepare
            playbackAnalyticsListener.markPlaybackStart()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }

        playerView.player = player
        currentPlayer = player
        currentView = playerView
        currentKey = mediaId
    }

    override fun preloadMedia(mediaId: String) {
        contentPreloader.schedulePreload(mediaId)
    }

    // ------------------------------------
    // Playback Controls
    // ------------------------------------

    override fun resume() {
        currentPlayer?.play()
    }

    override fun pause() {
        currentPlayer?.pause()
    }

    override fun detachPlayer() {
        currentView?.player = null
        currentView = null
        currentPlayer?.pause()
        currentPlayer = null
        currentKey = null
    }

    override fun getCurrentPlayer(): ExoPlayer? = currentPlayer

    private fun isValidMediaUrl(value: String): Boolean =
        value.startsWith("http://", true) || value.startsWith("https://", true)

    // ------------------------------------
    // Internal: Map MediaType to strategy
    // ------------------------------------
    private fun mapMediaTypeToStrategy(mediaType: MediaType) =
        when (mediaType) {
            MediaType.LIVE -> exoPlayerFactory.liveStrategy
            MediaType.VOD -> exoPlayerFactory.vodStrategy
        }
}
