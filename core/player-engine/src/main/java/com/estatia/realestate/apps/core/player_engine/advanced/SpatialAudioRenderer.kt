package com.estatia.realestate.apps.core.player_engine.advanced

import android.content.Context
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.AudioSink
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import javax.inject.Inject

/**
 * Handles Spatial / 3D audio capability detection.
 *
 * NOTE:
 * Android does not allow apps to force-enable or disable spatial audio.
 * It is controlled by the system and output device (e.g. headphones).
 */
@UnstableApi
class SpatialAudioRenderer @Inject constructor(
    context: Context,
    private val logger: LoggerInterface
) : AudioSink.Listener {

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val spatializer: Spatializer? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            audioManager.spatializer
        } else null

    fun isSpatialAudioSupported(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            spatializer?.isAvailable == true
        } else {
            false
        }
    }

    fun isSpatialAudioEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            spatializer?.isEnabled == true
        } else {
            false
        }
    }

    fun logSpatialAudioStatus() {
        logger.i(
            "Spatial Audio -> supported=${isSpatialAudioSupported()}, " +
                    "enabled=${isSpatialAudioEnabled()}"
        )
    }

    override fun onAudioSinkError(audioSinkError: Exception) {
        logger.e("AudioSink error: ${audioSinkError.message}")
    }

    override fun onOffloadBufferEmptying() {}
    override fun onOffloadBufferFull() {}
    override fun onPositionDiscontinuity() {}
    override fun onPositionAdvancing(playoutStartSystemTimeMs: Long) {}

    override fun onUnderrun(
        bufferSize: Int,
        bufferSizeMs: Long,
        elapsedSinceLastFeedMs: Long
    ) {
        logger.w(
            "Audio underrun: bufferSize=$bufferSize, " +
                    "bufferSizeMs=$bufferSizeMs, elapsed=$elapsedSinceLastFeedMs"
        )
    }

    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {}
}
