import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Surface
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
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

    val exoPlayer: ExoPlayer

    private val bandwidthMeter: DefaultBandwidthMeter =
        DefaultBandwidthMeter.Builder(context).build()

    private val cache: Cache = SimpleCache(
        File(context.cacheDir, "exo_cache"),
        NoOpCacheEvictor(), // Updated cache evictor
        null
    )

    private val trackSelector: DefaultTrackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setMaxVideoSize(1920, 1080) // Set both width and height
            .setPreferredTextLanguage("en")
            .build()
    }

    private val analyticsCollector: AnalyticsCollector = AnalyticsCollector.Factory.newInstance()
    private val playerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector(androidx.media3.exoplayer.MediaCodecSelector.DEFAULT) // Updated codec selector

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
            }
    }

    // Rest of your class remains mostly the same with the following changes:

    private fun createMediaSource(mediaItem: MediaItem): MediaSource {
        return ProgressiveMediaSource.Factory(
            createCacheDataSourceFactory(),
            DefaultExtractorsFactory() // Add extractor factory
        )
            .setDrmSessionManagerProvider { createDrmSessionManager() }
            .createMediaSource(mediaItem)
    }

    private fun createCacheDataSourceFactory(): DataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(
                OkHttpDataSource.Factory(OkHttpClient.Builder()
                    .protocols(listOf(Protocol.QUIC, Protocol.HTTP_2, Protocol.HTTP_1_1))
                    .build())
                    .setUserAgent(config.userAgent)
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    // Updated network type detection
    private fun getMaxVideoSizeForCurrentNetwork(): Pair<Int, Int> {
        return when (getNetworkType()) {
            NetworkType.TYPE_5G -> 3840 to 2160
            NetworkType.TYPE_4G -> 1920 to 1080
            else -> 1280 to 720
        }
    }

    private fun createDrmSessionManager(): DrmSessionManager {
        return config.drmConfig?.let { drmConfig ->
            val mediaDrmCallback = HttpMediaDrmCallback(
                drmConfig.licenseUrl,
                OkHttpDataSource.Factory(OkHttpClient())
            )

            DefaultDrmSessionManager.Builder()
                .setUuid(drmConfig.uuid)
                .setMultiSession(drmConfig.multiSession)
                .build(mediaDrmCallback)
        } ?: DrmSessionManager.DRM_UNSUPPORTED
    }

    // Add network type detection
    private fun getNetworkType(): NetworkType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    enum class NetworkType { TYPE_4G, TYPE_5G, UNKNOWN }

    // Updated PlayerConfig with DRM license URL
    data class DrmConfig(
        val uuid: UUID,
        val licenseUrl: String,
        val multiSession: Boolean = false,
        val offlineLicenseKeySetId: ByteArray? = null
    ) {
        // Keep existing equals/hashCode
    }

    // Remove OverlayEffect reference unless you have a custom implementation
}