package com.estatia.realestate.apps.core.player_engine.configuration

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import javax.inject.Inject

@UnstableApi
class PlayerConfigurationFactory @Inject constructor(
    private val streamingPipeline: IStreamingPipeline,
    private val playbackConfigurationProvider: IPlaybackConfigurationProvider
) : IPlayerConfigurationFactory {

    override fun create(
        uri: Uri,
        mediaType: MediaType
    ): PlayerConfiguration {

        val mediaItem = streamingPipeline.createMediaItem(uri, mediaType)

        val mediaSourceFactory = streamingPipeline.mediaSourceFactory()

        val loadControl = playbackConfigurationProvider
            .createLoadControl(mediaType)

        val speedControl = playbackConfigurationProvider
            .createPlaybackSpeedControl(mediaType)

        return PlayerConfiguration(
            mediaItem = mediaItem,
            mediaSourceFactory = mediaSourceFactory,
            loadControl = loadControl,
            livePlaybackSpeedControl = speedControl
        )
    }
}