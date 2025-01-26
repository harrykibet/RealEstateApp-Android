package com.application.real_estate_app.core.utils.media_players.exoplayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Surface
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.analytics.AnalyticsCollector
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.extractor.DefaultExtractorsFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.io.File
import java.util.UUID

@UnstableApi
class MediaPlayer private constructor(
    private val context: Context,
    private val config: PlayerConfig
) : Player.Listener {

    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val exoPlayer: ExoPlayer = createExoPlayer()
    private val cache = createCache()
    private val trackSelector = createTrackSelector()
    private val analyticsCollector = AnalyticsCollector.Factory.newInstance()

    init {
        configurePlayerDefaults()
    }

    // region Public API
    fun prepareContent(mediaItem: MediaItem, surface: Surface) {
        componentScope.launch {
            exoPlayer.run {
                setVideoSurface(surface)
                setMediaSource(createMediaSource(mediaItem), config.initialPositionMs)
                prepare()
                trackSelector.parameters = trackSelector.buildUponParameters()
                    .setMaxVideoSize(getMaxVideoSizeForCurrentNetwork())
                    .build()
            }
        }
    }

    fun release() {
        componentScope.cancel()
        exoPlayer.release()
        cache.release()
    }
    // endregion

    // region Player Configuration
    private fun createExoPlayer(): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context).apply {
            setEnableDecoderFallback(true)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        }

        return ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setBandwidthMeter(DefaultBandwidthMeter.Builder(context).build())
            .setLoadControl(createAdaptiveLoadControl())
            .setSeekParameters(SeekParameters.CLOSEST_SYNC)
            .setHandleAudioBecomingNoisy(true)
            .setAnalyticsCollector(analyticsCollector)
            .build()
    }

    private fun configurePlayerDefaults() {
        exoPlayer.apply {
            addListener(this@MediaPlayer)
            playWhenReady = config.autoPlay
            repeatMode = Player.REPEAT_MODE_ONE
            setAudioAttributes(AudioAttributes.DEFAULT, true)
        }
    }
    // endregion

    // region Media Source Handling
    private fun createMediaSource(mediaItem: MediaItem): MediaSource =
        ProgressiveMediaSource.Factory(
            createCacheDataSourceFactory(),
            DefaultExtractorsFactory()
        ).setDrmSessionManagerProvider { createDrmSessionManager() }
            .createMediaSource(mediaItem)

    private fun createCacheDataSourceFactory(): DataSource.Factory =
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(
                OkHttpDataSource.Factory(
                    OkHttpClient.Builder()
                        .protocols(listOf(Protocol.QUIC, Protocol.HTTP_2, Protocol.HTTP_1_1))
                        .build()
                ).setUserAgent(config.userAgent)
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    // endregion

    // region Adaptive Playback
    private fun createAdaptiveLoadControl(): LoadControl =
        DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                config.minBufferMs,
                config.maxBufferMs,
                config.playbackBufferMs,
                config.rebufferBufferMs
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

    private fun getMaxVideoSizeForCurrentNetwork(): VideoSize {
        val (width, height) = when (context.getNetworkType()) {
            NetworkType.TYPE_5G -> 3840 to 2160
            NetworkType.TYPE_4G -> 1920 to 1080
            else -> 1280 to 720
        }
        return VideoSize(width, height)
    }
    // endregion

    // region DRM Management
    private fun createDrmSessionManager(): DrmSessionManager =
        config.drmConfig?.let { drmConfig ->
            DefaultDrmSessionManager.Builder()
                .setUuid(drmConfig.uuid)
                .setMultiSession(drmConfig.multiSession)
                .build(
                    HttpMediaDrmCallback(
                        drmConfig.licenseUrl,
                        OkHttpDataSource.Factory(OkHttpClient())
                    )
                )
        } ?: DrmSessionManager.DRM_UNSUPPORTED
    // endregion

    // region Cache Management
    private fun createCache(): Cache = SimpleCache(
        File(context.cacheDir, "exo_cache"),
        LeastRecentlyUsedCacheEvictor(config.cacheSize),
        null
    )
    // endregion

    // region Track Selection
    private fun createTrackSelector(): DefaultTrackSelector =
        DefaultTrackSelector(context).apply {
            parameters = buildUponParameters()
                .setMaxVideoSize(1920, 1080)
                .setPreferredTextLanguage("en")
                .build()
        }
    // endregion

    companion object {
        fun create(context: Context, config: PlayerConfig): MediaPlayer {
            return MediaPlayer(context, config)
        }
    }
}

// region Support Classes
data class PlayerConfig(
    val cacheSize: Long = 512 * 1024 * 1024,
    val autoPlay: Boolean = true,
    val initialPositionMs: Long = 0L,
    val minBufferMs: Int = 15000,
    val maxBufferMs: Int = 30000,
    val playbackBufferMs: Int = 2500,
    val rebufferBufferMs: Int = 5000,
    val drmConfig: DrmConfig? = null,
    val userAgent: String = "RealEstateApp/1.0"
)

data class DrmConfig(
    val uuid: UUID,
    val licenseUrl: String,
    val multiSession: Boolean = false,
    val offlineLicenseKeySetId: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrmConfig

        if (uuid != other.uuid) return false
        if (licenseUrl != other.licenseUrl) return false
        if (multiSession != other.multiSession) return false
        if (offlineLicenseKeySetId != null) {
            if (other.offlineLicenseKeySetId == null) return false
            if (!offlineLicenseKeySetId.contentEquals(other.offlineLicenseKeySetId)) return false
        } else if (other.offlineLicenseKeySetId != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + licenseUrl.hashCode()
        result = 31 * result + multiSession.hashCode()
        result = 31 * result + (offlineLicenseKeySetId?.contentHashCode() ?: 0)
        return result
    }
}

enum class NetworkType { TYPE_4G, TYPE_5G, UNKNOWN }

fun Context.getNetworkType(): NetworkType {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return NetworkType.UNKNOWN
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.UNKNOWN

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
            when {
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NR) -> NetworkType.TYPE_5G
                else -> NetworkType.TYPE_4G
            }
        }
        else -> NetworkType.UNKNOWN
    }
}
// endregion