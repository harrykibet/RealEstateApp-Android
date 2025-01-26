package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.content.Context
import android.view.Surface
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.analytics.AnalyticsCollector
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.ExoMediaDrm
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

// 1. Advanced Player Module (Full Production Implementation)
@UnstableApi
class MediaPlayer private constructor(
    private val context: Context,
    private val config: PlayerConfig
) : Player.Listener {

    val exoPlayer: ExoPlayer

    private val bandwidthMeter: DefaultBandwidthMeter =
        DefaultBandwidthMeter.Builder(context).build()

    private val cache: Cache = SimpleCache(
        File(context.cacheDir, "exo_cache"),
        LruCacheEvictor(config.cacheSize),
        FileBasedIndexCache()
    )

    private val trackSelector: DefaultTrackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setMaxVideoSizeSd()
            .setPreferredTextLanguage("en")
            .build()
    }
    private val analyticsCollector: AnalyticsCollector? = null
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Region: Initialization
    init {

        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector(CodecSelector.DEFAULT_WITH_FALLBACK)

        exoPlayer = ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setBandwidthMeter(bandwidthMeter)
            .setLoadControl(createAdaptiveLoadControl())
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .setHandleAudioBecomingNoisy(true)
            .setAnalyticsCollector(analyticsCollector)
            .setUsePlatformDiagnostics(true)
            .build()
            .apply {
                addListener(this@MediaPlayer)
                playWhenReady = config.autoPlay
                repeatMode = Player.REPEAT_MODE_ONE
                setAudioAttributes(AudioAttributes.DEFAULT, true)
                setVideoEffects(listOf(OverlayEffect()))
            }
    }

    // Region: Core Playback Engine
    fun prepareContent(mediaItem: MediaItem, surface: Surface) {
        playerScope.launch {
            exoPlayer.setVideoSurface(surface)
            exoPlayer.setMediaSource(
                createMediaSource(mediaItem),
                config.initialPositionMs
            )
            exoPlayer.prepare()
            trackSelector.setParameters(
                trackSelector.buildUponParameters()
                    .setMaxVideoSize(getMaxVideoSizeForCurrentNetwork())
            )
        }
    }

    fun setVideoSurface(surface: Surface?) {
        exoPlayer.setVideoSurface(surface)
    }

    fun setVideoSurfaceSize(width: Int, height: Int) {
        exoPlayer.videoSize = VideoSize(width, height)
    }

    fun clearVideoSurface() {
        exoPlayer.clearVideoSurface()
    }


    private fun createMediaSource(mediaItem: MediaItem): MediaSource {
        return ProgressiveMediaSource.Factory(
            createCacheDataSourceFactory(),
            createExtractorFactory()
        )
            .setDrmSessionManagerProvider { createDrmSessionManager() }
            .createMediaSource(mediaItem)
    }

    private fun createCacheDataSourceFactory(): DataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(
                OkHttpDataSource.Factory(OkHttpClient.Builder()
                    .cache(null)
                    .protocols(listOf(Protocol.QUIC, Protocol.HTTP_2, Protocol.HTTP_1_1))
                    .addInterceptor(ChunkedDownloadInterceptor())
                    .build())
                    .setUserAgent(config.userAgent)
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            .setCacheKeyFactory { dataSpec -> dataSpec.key }
    }

    // Region: Adaptive Streaming
    private fun getMaxVideoSizeForCurrentNetwork(): Int {
        return when (bandwidthMeter.getNetworkType()) {
            C.NETWORK_TYPE_4G -> 1080
            C.NETWORK_TYPE_5G -> 2160
            else -> 720
        }
    }

    private fun createAdaptiveLoadControl(): LoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                config.minBufferMs,
                config.maxBufferMs,
                config.playbackBufferMs,
                config.rebufferBufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .setTargetBufferBytes(C.LENGTH_UNSET)
            .build()
    }

    // Region: DRM & Security
    private fun createDrmSessionManager(): DrmSessionManager {
        return if (config.drmConfig != null) {
            val drmSessionManager = DefaultDrmSessionManager.Builder()
                .setUuidAndExoMediaDrmProvider(config.drmConfig.uuid, ExoMediaDrm.AppManagedProvider)
                .setMultiSession(config.drmConfig.multiSession)
                .build(DefaultHttpDataSource.Factory())

            drmSessionManager.setMode(
                config.drmConfig.offlineLicenseKeySetId?.let { C.WIDEVINE_SECURITY_LEVEL_1 }
                    ?: C.WIDEVINE_SECURITY_LEVEL_3,
                config.drmConfig.offlineLicenseKeySetId
            )
            drmSessionManager
        } else {
            DrmSessionManager.DRM_UNSUPPORTED
        }
    }

    // Region: Memory Management
    fun release() {
        playerScope.cancel()
        exoPlayer.release()
        cache.release()
    }

    // Region: Configuration Classes
    data class PlayerConfig(
        val cacheSize: Long = 512 * 1024 * 1024,
        val autoPlay: Boolean = true,
        val initialPositionMs: Long = 0L,
        val minBufferMs: Int = 15000,
        val maxBufferMs: Int = 30000,
        val playbackBufferMs: Int = 2500,
        val rebufferBufferMs: Int = 5000,
        val drmConfig: DrmConfig? = null,
        val userAgent: String = "YourApp/1.0"
    )

    data class DrmConfig(
        val uuid: UUID,
        val multiSession: Boolean = false,
        val offlineLicenseKeySetId: ByteArray? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DrmConfig

            if (uuid != other.uuid) return false
            if (multiSession != other.multiSession) return false
            if (offlineLicenseKeySetId != null) {
                if (other.offlineLicenseKeySetId == null) return false
                if (!offlineLicenseKeySetId.contentEquals(other.offlineLicenseKeySetId)) return false
            } else if (other.offlineLicenseKeySetId != null) return false

            return true
        }

        override fun hashCode(): Int {
            var result = uuid.hashCode()
            result = 31 * result + multiSession.hashCode()
            result = 31 * result + (offlineLicenseKeySetId?.contentHashCode() ?: 0)
            return result
        }
    }

    companion object {
        fun create(context: Context, config: PlayerConfig): MediaPlayer {
            return MediaPlayer(context, config)
        }
    }
}
