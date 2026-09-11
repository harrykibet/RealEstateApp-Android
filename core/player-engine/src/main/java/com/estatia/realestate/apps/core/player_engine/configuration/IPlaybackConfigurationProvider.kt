package com.estatia.realestate.apps.core.player_engine.configuration

import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.LivePlaybackSpeedControl
import androidx.media3.exoplayer.LoadControl
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

// Justification: Required for low-level Media3 playback orchestration.
@UnstableApi
@Contract
interface IPlaybackConfigurationProvider {
    fun createLoadControl(mediaType: MediaType, environment: EnvironmentState): LoadControl
    fun createPlaybackSpeedControl(mediaType: MediaType, environment: EnvironmentState): LivePlaybackSpeedControl?
    fun createTrackSelectionParameters(matchScore: Float, environment: EnvironmentState): TrackSelectionParameters
}
