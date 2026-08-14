package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider,
    private val hdrConfiguration: HdrConfiguration,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val deviceUtils: IDeviceUtils
) : IPlayerConfigurationFactory {

    override suspend fun create(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        matchScore: Float,
        forceLegacyCodec: Boolean,
        title: String?,
        artist: String?
    ): PlayerConfiguration {
        val env = environmentCoordinator.environment.value
        
        // 🏎️ Capability-Aware Manifest Routing:
        // We don't just broadcast headers; we actively select the best manifest 
        // "Stack" for this device.
        val resolvedUri = if (needsCdnResolution(uri)) {
            val builder = uri.buildUpon()
            when {
                forceLegacyCodec -> builder.appendQueryParameter("codec", "baseline")
                deviceUtils.supportsAV1() -> builder.appendQueryParameter("codec", "av1")
                deviceUtils.supportsHEVC() -> builder.appendQueryParameter("codec", "hevc")
                else -> builder.appendQueryParameter("codec", "h264_high")
            }
            builder.build()
        } else {
            uri
        }

        val qualityHint = when {
            forceLegacyCodec -> "legacy"
            deviceUtils.supportsAV1() -> "av1"
            deviceUtils.supportsHEVC() -> "hevc"
            else -> "standard"
        }
        val mediaItem = streamingPipeline.createMediaItem(mediaId, resolvedUri, mediaType, title, artist, qualityHint)
        val mediaSourceFactory = streamingPipeline.mediaSourceFactory()
        val loadControl = playbackConfigurationProvider.createLoadControl(mediaType, env)
        val speedControl = playbackConfigurationProvider.createPlaybackSpeedControl(mediaType, env)
        val trackSelectionParameters = playbackConfigurationProvider.createTrackSelectionParameters(matchScore, env)

        return PlayerConfiguration(
            mediaItem = mediaItem,
            mediaSourceFactory = mediaSourceFactory,
            loadControl = loadControl,
            trackSelectionParameters = trackSelectionParameters,
            livePlaybackSpeedControl = speedControl,
            hdrMode = hdrConfiguration.getBestSupportedMode(env.thermalStatus)
        )
    }

    /**
     * Checks if the URI belongs to an internal Estatia media domain.
     */
    private fun needsCdnResolution(uri: Uri): Boolean {
        val host = uri.host ?: return false
        return host == "estatia.com" || host.endsWith(".estatia.com")
    }
}
