package com.application.real_estate_app.feature_mediaplayer.advanced

import android.content.Context
import android.media.AudioManager
import android.media.Spatializer
import android.os.Build
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderCounters
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import javax.inject.Inject

// 3D audio support
@UnstableApi
class SpatialAudioRenderer @Inject constructor(
    private val context: Context
) : AudioRendererEventListener {

    private var spatializer: Spatializer? = null

    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            spatializer = audioManager.spatializer
        }
    }

    fun enable3DAudio(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            spatializer?.setEnabled(
                Spatializer.SPATIALIZER_IMMERSIVE_LEVEL_MULTICHANNEL,
                enable
            )
        }
    }

    override fun onAudioEnabled(event: DecoderCounters) {
        enable3DAudio(true)
    }
}