package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.CdnSelector
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import javax.inject.Inject
import androidx.core.net.toUri

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider,
    private val cdnSelector: CdnSelector
) : IPlayerConfigurationFactory {

    override suspend fun create(
        mediaId: String,
        uri: Uri,
        mediaType: MediaType
    ): PlayerConfiguration {
        val resolvedUri = resolveViaCdn(uri)
        val mediaItem = streamingPipeline.createMediaItem(mediaId, resolvedUri, mediaType)
        val mediaSourceFactory = streamingPipeline.mediaSourceFactory()
        val loadControl = playbackConfigurationProvider.createLoadControl(mediaType)
        val speedControl = playbackConfigurationProvider.createPlaybackSpeedControl(mediaType)

        return PlayerConfiguration(
            mediaItem = mediaItem,
            mediaSourceFactory = mediaSourceFactory,
            loadControl = loadControl,
            livePlaybackSpeedControl = speedControl
        )
    }

    /**
     * Rewrites the request host to the currently healthiest CDN endpoint.
     * Only applies to internal Estatia media domains.
     * Falls back to the original URI on selection failure — CDN routing is an
     * optimization, not a playback precondition.
     */
    private suspend fun resolveViaCdn(uri: Uri): Uri {
        val host = uri.host ?: return uri
        if (!host.contains("estatia.com")) return uri

        val endpoint = runCatching { cdnSelector.select() }.getOrNull() ?: return uri
        val endpointUri = endpoint.baseUrl.toUri()
        return uri.buildUpon()
            .scheme(endpointUri.scheme ?: uri.scheme)
            .authority(endpointUri.authority ?: uri.authority)
            .build()
    }
}
