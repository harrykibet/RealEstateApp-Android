package com.application.real_estate_app.feature_home.exoplayer

import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class ExoPlayerManager @Inject constructor(@ApplicationContext private val context: Context) :
    DefaultLifecycleObserver {

    private var player: ExoPlayer? = null
    private var currentMediaUri: String? = null
    private var currentSurface: PlayerView? = null
    private val userAgent = "RentalApp/1.0.0"

    // Cache configuration
    private val cacheDirectory: File = File(context.cacheDir, "exoPlayerCache")
    @Suppress("DEPRECATION")
    private val simpleCache = SimpleCache(cacheDirectory, LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024)) // 200MB cache
    private val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
        .setCache(simpleCache)
        .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory().setUserAgent(userAgent))
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

    // Watch duration tracking
    private val videoWatchDurationMap = mutableMapOf<String, Long>()
    private var playbackStartTime = 0L


    // Initialize ExoPlayer
    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(context)
                .setTrackSelector(DefaultTrackSelector(context))
                .setLoadControl(
                    DefaultLoadControl.Builder()
                        .setBufferDurationsMs(5000, 20000, 1500, 2000) // Buffer settings
                        .build()
                )
                .build().apply {
                    addListener(PlayerEventListener())
                }
        }
    }

    // Attach ExoPlayer to a PlayerView
    fun attachPlayerToView(playerView: PlayerView, uri: String) {
        if (currentMediaUri != uri) {
            prepareMedia(uri)
        }
        currentSurface?.player = null // Detach previous surface
        currentSurface = playerView
        playerView.player = player
    }

    // Prepare media for playback, including cache logic
    private fun prepareMedia(uri: String) {
        initializePlayer()
        val mediaSource = buildMediaSource(uri)
        player?.apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
        currentMediaUri = uri
        playbackStartTime = System.currentTimeMillis()
    }

    // Build media source with offline caching
    private fun buildMediaSource(uri: String): MediaSource {
        val mediaItem = MediaItem.fromUri(uri)
        return if (uri.contains(".m3u8")) { // HLS
            HlsMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)
        } else { // Progressive (MP4)
            ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)
        }
    }

    // Preload multiple video URIs into the player
    @Suppress("unused")
    fun preloadMedia(uris: List<String>) {
        if (uris.isEmpty()) return // Exit if there are no URIs to preload

        initializePlayer() // Ensure the player is initialized
        player?.let { exoPlayer ->
            uris.forEach { uri ->
                val mediaSource = buildMediaSource(uri)
                exoPlayer.addMediaSource(mediaSource)
            }
            exoPlayer.prepare() // Prepare the player after adding all media sources
        } ?: run {
            // Log or handle the case where the player is not initialized
            println("Error: Player not initialized.")
        }
    }

    // Preload a single video URI into the player
    fun preloadMedia(uri: String) {
        if (uri.isEmpty()) return // Exit if the URI is empty

        initializePlayer() // Ensure the player is initialized
        val mediaSource = buildMediaSource(uri)
        player?.let { exoPlayer ->
            exoPlayer.addMediaSource(mediaSource)
            exoPlayer.prepare() // Prepare the player after adding the media source
            Log.d("ExoPlayerManager", "Preloaded video: $uri")
        } ?: run {
            // Log or handle the case where the player is not initialized
            println("Error: Player not initialized.")
        }
    }

    // Detach current surface
    fun detachPlayer() {
        currentSurface?.player = null
        currentSurface = null
    }

    // Pause playback
    fun pause() {
        player?.playWhenReady = false
        calculateWatchDuration()
    }

    // Resume playback
    fun resume() {
        player?.playWhenReady = true
        playbackStartTime = System.currentTimeMillis()
    }

    // Release ExoPlayer resources
    fun releasePlayer() {
        calculateWatchDuration()
        player?.release()
        player = null
        currentMediaUri = null
        currentSurface = null
        videoWatchDurationMap.clear()
    }

    // Track watch duration
    private fun calculateWatchDuration() {
        val currentUri = currentMediaUri ?: return
        val duration = System.currentTimeMillis() - playbackStartTime
        videoWatchDurationMap[currentUri] = (videoWatchDurationMap[currentUri] ?: 0) + duration
        Log.d("ExoPlayerManager", "Watch duration for $currentUri: ${videoWatchDurationMap[currentUri]} ms")
    }

    // Visibility-based playback
    @Suppress("unused")
    fun handleVisibilityChange(isVisible: Boolean) {
        if (isVisible) resume() else pause()
    }

    // Implement Lifecycle Observer Methods
    override fun onPause(owner: LifecycleOwner) {
        pause()
    }

    override fun onResume(owner: LifecycleOwner) {
        resume()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        releasePlayer()
    }

    // Handle playback errors
    private inner class PlayerEventListener : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.e("ExoPlayerManager", "Playback error: ${error.message}")
            // Retry logic or user-friendly error handling
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    Log.d("ExoPlayerManager", "Buffering...")
                }
                Player.STATE_READY -> {
                    Log.d("ExoPlayerManager", "Ready to play.")
                }
                Player.STATE_ENDED -> {
                    Log.d("ExoPlayerManager", "Playback ended.")
                }
                Player.STATE_IDLE -> {
                    Log.d("ExoPlayerManager", "Player idle.")
                }
            }
        }
    }
}
