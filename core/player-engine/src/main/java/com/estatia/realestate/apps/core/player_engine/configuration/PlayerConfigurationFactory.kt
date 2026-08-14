package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import javax.inject.Inject

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider,
    private val hdrConfiguration: HdrConfiguration,
    private val environmentCoordinator: EnvironmentCoordinator
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
        
        // 🏎️ Architecture Shift: Do NOT resolve CDN eagerly here.
        // By giving the player the original internal URI (e.g. media.estatia.com),
        // we ensure that relative segment paths in manifests are resolved back 
        // to that same internal domain. 
        // Our CdnFailoverDataSource then intercepts these requests and performs
        // segment-level routing and failover transparently.
        val resolvedUri = if (needsCdnResolution(uri) && forceLegacyCodec) {
            uri.buildUpon().appendQueryParameter("codec", "h264_baseline").build()
        } else {
            uri
        }

        val qualityHint = if (forceLegacyCodec) "legacy" else "standard"
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
