package com.estatia.realestate.apps.core.player_engine.configuration

import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.DynamicBitratePolicy
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import javax.inject.Inject
import kotlin.math.roundToInt

@UnstableApi
class DynamicBitrateController @Inject constructor(
    private val bitratePolicy: DynamicBitratePolicy
) {

    /**
     * Applies adaptive bitrate constraints based on
     * media type, current environment, and real-time buffer health.
     *
     * Safe to call repeatedly. Idempotent.
     */
    fun apply(
        player: ExoPlayer,
        mediaType: MediaType,
        environment: EnvironmentState,
        bufferSeconds: Double = 5.0,
        startupPhase: Boolean = false
    ) {
        checkConfinement()
        val targetBitrate =
            bitratePolicy.calculateMaxVideoBitrate(
                mediaType = mediaType,
                environment = environment,
                bufferSeconds = bufferSeconds
            )

        val effectiveBitrate = if (startupPhase) {
            (targetBitrate * 0.55).roundToInt().coerceAtLeast(500_000)
        } else {
            targetBitrate
        }

        val currentBitrate = player.trackSelectionParameters.maxVideoBitrate
        if (currentBitrate == effectiveBitrate) return

        player.trackSelectionParameters =
            player.trackSelectionParameters
                .buildUpon()
                .setMaxVideoBitrate(effectiveBitrate)
                .build()
    }

    private fun checkConfinement() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("DynamicBitrateController must only be accessed from the Main thread.")
        }
    }
}
