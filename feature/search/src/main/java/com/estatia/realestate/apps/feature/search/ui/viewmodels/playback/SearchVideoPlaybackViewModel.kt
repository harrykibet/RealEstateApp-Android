package com.estatia.realestate.apps.feature.search.ui.viewmodels.playback

import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_ui.viewmodels.BaseVideoPlaybackViewModel
import com.estatia.realestate.apps.core.domain.interfaces.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class SearchVideoPlaybackViewModel @Inject constructor(
    coordinator: VideoPlaybackCoordinator,
    environmentCoordinator: EnvironmentCoordinator,
    userRepository: IUserRepository
) : BaseVideoPlaybackViewModel(coordinator, environmentCoordinator, userRepository)
