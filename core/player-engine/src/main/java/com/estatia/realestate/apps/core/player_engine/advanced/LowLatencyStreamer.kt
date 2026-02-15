package com.estatia.realestate.apps.core.player_engine.advanced

import androidx.media3.common.MediaItem
import javax.inject.Inject

class LowLatencyStreamer @Inject constructor() {

    fun createLowLatencyMediaItem(uri: String): MediaItem {

        val liveConfig = MediaItem.LiveConfiguration.Builder()
            .setTargetOffsetMs(1000L)
            .setMinPlaybackSpeed(0.97f)
            .setMaxPlaybackSpeed(1.03f)
            .build()

        return MediaItem.Builder()
            .setUri(uri)
            .setLiveConfiguration(liveConfig)
            .build()
    }
}
