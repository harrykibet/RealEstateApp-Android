package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.ui.PlayerView
import com.estatia.realestate.apps.core.domain.interfaces.IPlayer
import com.estatia.realestate.apps.core.player_engine.streaming.CacheManager
import com.estatia.realestate.apps.core.player_engine.streaming.ContentPreloader
import javax.inject.Inject
import javax.inject.Singleton
import com.estatia.realestate.apps.core.player_engine.advanced.LowLatencyStreamer
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.perfomance.PlayerPerformanceOptimizer

@UnstableApi
@Singleton
class ExoPlayerInstanceManager
@Inject constructor(
    private val context: Context,
    private val bandwidthMeter: BandwidthMeter,
    private val contentPreloader: ContentPreloader,
    private val lowLatencyStreamer: LowLatencyStreamer,
    private val performanceOptimizer: PlayerPerformanceOptimizer,
    private val playbackAnalyticsListener: PlaybackAnalyticsListener,
    cacheManager: CacheManager
) : IPlayer {

    private val mediaSourceFactory =
        ProgressiveMediaSource.Factory(
            cacheManager.createCacheDataSourceFactory()
        )

    private val playerPool = mutableListOf<ExoPlayer>()
    private val activePlayers = mutableMapOf<String, ExoPlayer>()

    @Volatile private var currentPlayer: ExoPlayer? = null
    @Volatile private var currentView: PlayerView? = null
    @Volatile private var currentKey: String? = null

    // ------------------------------------
    // Player Builder (Single Source)
    // ------------------------------------

    private fun createConfiguredPlayer(): ExoPlayer {

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1500,
                3000,
                500,
                1000
            )
            .build()

        val liveSpeedControl = DefaultLivePlaybackSpeedControl.Builder()
            .setFallbackMinPlaybackSpeed(0.97f)
            .setFallbackMaxPlaybackSpeed(1.03f)
            .build()

        val builder = ExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setLivePlaybackSpeedControl(liveSpeedControl)

        // 🔋 Apply performance optimization here
        performanceOptimizer.optimize(builder)

        return builder.build().also { player ->
            player.addAnalyticsListener(playbackAnalyticsListener)
            playerPool.add(player)
        }
    }


    // ------------------------------------
    // Player Pool
    // ------------------------------------

    override fun acquirePlayer(mediaId: String): ExoPlayer {
        activePlayers[mediaId]?.let { return it }

        val reusable = playerPool.firstOrNull { !it.isPlaying }

        val player = reusable?.also {
            it.stop()
            it.clearMediaItems()
        } ?: createConfiguredPlayer()

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
        isLive: Boolean,
        mediaId: String
    ) {
        detachPlayer()

        val player = acquirePlayer(mediaId)

        if (isValidMediaUrl(mediaId)) {

            val mediaItem = buildMediaItem(
                url = mediaId,
                isLive = isLive
            )

            // 📊 Mark playback start BEFORE prepare
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

    private fun buildMediaItem(
        url: String,
        isLive: Boolean
    ): MediaItem {

        return if (isLive) {
            lowLatencyStreamer.createLowLatencyMediaItem(url)
        } else {
            MediaItem.fromUri(url)
        }
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

    private fun isValidMediaUrl(value: String): Boolean {
        return value.startsWith("http://", true) ||
                value.startsWith("https://", true)
    }
}
