package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.DynamicBitratePolicy
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import javax.inject.Inject

@UnstableApi
class DynamicBitrateController @Inject constructor(
    private val bitratePolicy: DynamicBitratePolicy
) {

    /**
     * Applies adaptive bitrate constraints based on
     * media type and current environment.
     *
     * Safe to call repeatedly. Idempotent.
     */
    fun apply(
        player: ExoPlayer,
        mediaType: MediaType,
        environment: EnvironmentState
    ) {
        val targetBitrate =
            bitratePolicy.calculateMaxVideoBitrate(
                mediaType = mediaType,
                environment = environment
            )

        val currentBitrate =
            player.trackSelectionParameters.maxVideoBitrate

        if (currentBitrate == targetBitrate) return

        player.trackSelectionParameters =
            player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoBitrate(targetBitrate)
                .build()
    }
}