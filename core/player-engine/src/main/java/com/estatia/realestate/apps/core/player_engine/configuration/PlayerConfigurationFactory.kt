package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.CdnSelector
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.HdrConfiguration
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import javax.inject.Inject
import androidx.core.net.toUri

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider,
    private val cdnSelector: CdnSelector,
    private val config: IConfigProvider,
    private val hdrConfiguration: HdrConfiguration,
    private val environmentCoordinator: EnvironmentCoordinator,
    private val logger: ILogger
) : IPlayerConfigurationFactory {

    override suspend fun create(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType,
        forceLegacyCodec: Boolean,
        title: String?,
        artist: String?
    ): PlayerConfiguration {
        // ⏱️ Optimization: Only wait for config if we actually need it for CDN resolution.
        // Idle players use Uri.EMPTY and shouldn't be blocked.
        val env = environmentCoordinator.environment.value
        val resolvedUri = if (needsCdnResolution(uri)) {
            config.awaitReady()
            val base = resolveViaCdn(uri)
            if (forceLegacyCodec) {
                base.buildUpon().appendQueryParameter("codec", "h264_baseline").build()
            } else base
        } else {
            uri
        }

        val mediaItem = streamingPipeline.createMediaItem(mediaId, resolvedUri, mediaType, title, artist)
        val mediaSourceFactory = streamingPipeline.mediaSourceFactory()
        val loadControl = playbackConfigurationProvider.createLoadControl(mediaType, env)
        val speedControl = playbackConfigurationProvider.createPlaybackSpeedControl(mediaType, env)

        return PlayerConfiguration(
            mediaItem = mediaItem,
            mediaSourceFactory = mediaSourceFactory,
            loadControl = loadControl,
            livePlaybackSpeedControl = speedControl,
            hdrMode = hdrConfiguration.getBestSupportedMode(env.thermalStatus)
        )
    }

    /**
     * Rewrites the request host to the currently healthiest CDN endpoint.
     * Only applies to internal Estatia media domains.
     * Falls back to the original URI on selection failure — CDN routing is an
     * optimization, not a playback precondition.
     */
    private fun resolveViaCdn(uri: Uri): Uri {
        if (!needsCdnResolution(uri)) return uri

        val endpoint = try {
            cdnSelector.select()
        } catch (e: Exception) {
            logger.e("CDN", "Resolution failure for $uri. Falling back to raw host.", e)
            null
        } ?: return uri
        
        val endpointUri = endpoint.baseUrl.toUri()
        return uri.buildUpon()
            .scheme(endpointUri.scheme ?: uri.scheme)
            .authority(endpointUri.authority ?: uri.authority)
            .build()
    }

    private fun needsCdnResolution(uri: Uri): Boolean {
        val host = uri.host ?: return false
        return host.contains("estatia.com")
    }
}
