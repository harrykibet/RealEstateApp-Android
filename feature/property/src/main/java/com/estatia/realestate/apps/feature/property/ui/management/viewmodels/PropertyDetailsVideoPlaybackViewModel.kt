package com.estatia.realestate.apps.feature.property.ui.management.viewmodels

import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.common.annotations.ViewModelMarker
import com.estatia.realestate.apps.core.player_ui.viewmodels.BaseVideoPlaybackViewModel
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@ViewModelMarker
@HiltViewModel
class PropertyDetailsVideoPlaybackViewModel @Inject constructor(
    coordinator: VideoPlaybackCoordinator,
    environmentCoordinator: EnvironmentCoordinator,
    userRepository: IUserRepository
) : BaseVideoPlaybackViewModel(coordinator, environmentCoordinator, userRepository)
