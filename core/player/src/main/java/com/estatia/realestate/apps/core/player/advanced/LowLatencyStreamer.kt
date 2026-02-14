package com.estatia.realestate.apps.core.player.advanced

import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

// LL-HLS/LL-DASH implementation
@UnstableApi
class LowLatencyStreamer @Inject constructor(
    private val exoPlayer: ExoPlayer
) {
    fun enableLowLatencyMode() {
        exoPlayer.playbackParameters = PlaybackParameters(1f, 1f) // 1s buffer
        exoPlayer.setPriorityTaskManager(null) // Bypass default throttling
    }
}