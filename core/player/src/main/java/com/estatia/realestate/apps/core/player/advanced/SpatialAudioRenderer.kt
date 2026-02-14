package com.estatia.realestate.apps.core.player.advanced

import android.content.Context
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.audio.AudioSink
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import java.lang.Exception
import javax.inject.Inject

// 3D Audio Support
@Suppress("Unused")
@UnstableApi
class SpatialAudioRenderer @Inject constructor(
    context: Context,
    private val logger: LoggerInterface
) : AudioSink.Listener {

    private var spatializer: Spatializer? = null

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            spatializer = audioManager.spatializer.takeIf {
                it.isAvailable
            }
        }
    }

    // Method to attempt enabling or disabling spatial audio features based on device support
    fun enable3DAudio(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {
            spatializer?.let {
                // Check if spatializer is available
                if (it.isAvailable) {
                    // No direct way to enable/disable immersive audio, but can check availability
                    // Optionally, you can query the current level, but it's not a must here
                    // Enable/disable feature based on availability
                    // At this stage, we can only inform about spatial audio being supported
                    if (enable) {
                        // Inform that spatial audio is enabled if available
                        // You can add additional logic based on your needs here
                        logger.i("3D Audio supported by device")
                    } else {
                        // Inform that spatial audio is disabled
                        // Add any additional cleanup logic here
                        logger.i("3D Audio not supported by device")
                    }
                }
            }
        }
    }

    // Updated AudioSink.Listener methods (kept empty for now)
    override fun onAudioSinkError(audioSinkError: Exception) {}
    override fun onOffloadBufferEmptying() {}
    override fun onOffloadBufferFull() {}
    override fun onPositionDiscontinuity() {}
    override fun onPositionAdvancing(playoutStartSystemTimeMs: Long) {}
    override fun onUnderrun(bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {}
    override fun onSkipSilenceEnabledChanged(skipSilenceEnabled: Boolean) {}
}
