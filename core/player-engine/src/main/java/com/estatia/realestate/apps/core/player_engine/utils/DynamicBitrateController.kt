package com.estatia.realestate.apps.core.player_engine.utils

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import javax.inject.Inject

@UnstableApi
class DynamicBitrateController @Inject constructor(
    private val bitratePolicy: DynamicBitratePolicy
) {

    fun attach(player: ExoPlayer, mediaType: MediaType) {
        apply(player, mediaType)
    }

    fun detach(player: ExoPlayer) {
        // no-op for now
    }

    fun onEnvironmentChanged(player: ExoPlayer, mediaType: MediaType) {
        apply(player, mediaType)
    }

    private fun apply(player: ExoPlayer, mediaType: MediaType) {
        val maxBitrate = bitratePolicy.calculateMaxVideoBitrate(mediaType)

        val current = player.trackSelectionParameters.maxVideoBitrate
        if (current == maxBitrate) return

        player.trackSelectionParameters =
            player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoBitrate(maxBitrate)
                .build()
    }
}
