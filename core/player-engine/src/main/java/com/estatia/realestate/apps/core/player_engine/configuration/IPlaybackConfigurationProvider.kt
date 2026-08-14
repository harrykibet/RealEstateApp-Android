package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState

@UnstableApi
interface IPlaybackConfigurationProvider {
    fun createLoadControl(mediaType: MediaType, environment: EnvironmentState): LoadControl
    fun createPlaybackSpeedControl(mediaType: MediaType, environment: EnvironmentState): LivePlaybackSpeedControl?
    fun createTrackSelectionParameters(matchScore: Float, environment: EnvironmentState): TrackSelectionParameters
}
