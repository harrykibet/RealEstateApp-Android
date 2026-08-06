package com.estatia.realestate.apps.feature.home.ui.viewModels.playback

import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_ui.viewmodels.BaseVideoPlaybackViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeVideoPlaybackViewModel @Inject constructor(
    coordinator: VideoPlaybackCoordinator
) : BaseVideoPlaybackViewModel(coordinator)
