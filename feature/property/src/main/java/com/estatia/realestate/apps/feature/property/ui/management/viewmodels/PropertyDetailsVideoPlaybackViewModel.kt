package com.estatia.realestate.apps.feature.property.ui.management.viewmodels

import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_ui.viewmodels.BaseVideoPlaybackViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class PropertyDetailsVideoPlaybackViewModel @Inject constructor(
    coordinator: VideoPlaybackCoordinator,
    environmentCoordinator: EnvironmentCoordinator
) : BaseVideoPlaybackViewModel(coordinator, environmentCoordinator)
