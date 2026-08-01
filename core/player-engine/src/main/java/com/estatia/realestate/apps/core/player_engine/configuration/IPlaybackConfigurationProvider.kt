package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType

@UnstableApi
interface IPlaybackConfigurationProvider {
    fun createLoadControl(mediaType: MediaType): LoadControl
    fun createPlaybackSpeedControl(mediaType: MediaType): LivePlaybackSpeedControl?
}
