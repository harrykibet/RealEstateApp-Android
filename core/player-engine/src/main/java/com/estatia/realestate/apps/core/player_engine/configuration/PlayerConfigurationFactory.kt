package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.player_engine.streaming.StreamingUriResolver
import javax.inject.Inject

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider,
    private val hdrConfiguration: HdrConfiguration,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val deviceUtils: IDeviceUtils,
    private val uriResolver: StreamingUriResolver
) : IPlayerConfigurationFactory {

    override suspend fun create(
        mediaId: String,
        uri: MediaReference,
        mediaType: MediaType,
        matchScore: Float,
        forceLegacyCodec: Boolean,
        title: String?,
        artist: String?
    ): PlayerConfiguration {
        val env = environmentCoordinator.environment.value
        
        val platformUri = uri.value.toUri()
        // 🏎️ Capability-Aware Manifest Routing:
        // We don't just broadcast headers; we actively select the best manifest 
        // "Stack" for this device.
        val resolvedUri = uriResolver.resolve(platformUri, forceLegacyCodec)

        val qualityHint = if (forceLegacyCodec) "legacy" else deviceUtils.getVideoQualityHint()
        
        val mediaItem = streamingPipeline.createMediaItem(mediaId, MediaReference(resolvedUri.toString()), mediaType, title, artist, qualityHint)
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
}
