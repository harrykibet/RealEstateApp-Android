package com.estatia.realestate.apps.feature.player.core

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer
import com.estatia.realestate.apps.feature.player.streaming.CacheManager
import com.estatia.realestate.apps.feature.player.streaming.ContentPreloader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages ExoPlayer instances for feed-style (e.g. TikTok-like) playback.
 * Supports one active player attached to a view; [resume]/[pause] operate on that player.
 * For playback to start, [attachPlayerToView] must be called with a valid media URL (string starting with "http").
 */
@Singleton
class ExoPlayerInstanceManager
@Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val contentPreloader: ContentPreloader,
    private val cacheManager: CacheManager
) : IExoplayer {

    private val mediaSourceFactory = ProgressiveMediaSource.Factory(cacheManager.createCacheDataSourceFactory())
    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    @Volatile
    private var currentPlayer: ExoPlayer? = null

    @Volatile
    private var currentView: PlayerView? = null

    @Volatile
    private var currentKey: String? = null

    override fun acquirePlayer(mediaId: String): ExoPlayer {
        val existing = activePlayers[mediaId]
        if (existing != null) return existing
        val player = playerPool.firstOrNull { !it.isPlaying }?.also {
            it.stop()
            it.clearMediaItems()
        } ?: createNewPlayer()
        activePlayers[mediaId] = player
        return player
    }

    private fun createNewPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .also { playerPool.add(it) }
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

    override fun preloadMedia(mediaId: String) {
        contentPreloader.schedulePreload(mediaId)
    }

    /**
     * Attaches a player to [playerView] and loads media identified by [mediaId].
     * If [mediaId] is a valid HTTP(S) URL, it is used as the media source; otherwise no media is loaded.
     * Callers should pass the media URL here when they have it (e.g. property.videoUrls.first()) so playback works.
     */
    override fun attachPlayerToView(playerView: PlayerView, mediaId: String) {
        detachPlayer()
        val player = acquirePlayer(mediaId)
        if (isValidMediaUrl(mediaId)) {
            val mediaItem = MediaItem.fromUri(Uri.parse(mediaId))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
        playerView.player = player
        currentPlayer = player
        currentView = playerView
        currentKey = mediaId
    }

    override fun detachPlayer() {
        currentView?.player = null
        currentView = null
        currentPlayer?.pause()
        currentPlayer = null
        currentKey = null
    }

    override fun getCurrentPlayer(): ExoPlayer? {
        return currentPlayer
    }

    override fun resume() {
        currentPlayer?.play()
    }

    override fun pause() {
        currentPlayer?.pause()
    }

    private fun isValidMediaUrl(value: String): Boolean {
        return value.startsWith("http://", ignoreCase = true) || value.startsWith("https://", ignoreCase = true)
    }
}