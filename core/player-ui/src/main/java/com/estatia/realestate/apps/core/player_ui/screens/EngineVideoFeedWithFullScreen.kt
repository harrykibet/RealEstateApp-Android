package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.feature.VideoItem
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun EngineVideoFeedWithFullScreen(
    videos: List<VideoItem>,
    viewModel: VideoPlaybackViewModel = viewModel() // Injected Hilt ViewModel
) {
    var fullScreenVideo by remember { mutableStateOf<VideoItem?>(null) }

    // Feed with shared ViewModel
    EngineVideoFeed(
        videos = videos,
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize()
    )

    // Fullscreen overlay
    fullScreenVideo?.let { video ->
        EngineVideoFullScreen(
            mediaId = video.mediaId,
            mediaType = MediaType.VOD,
            onExitFullScreen = { fullScreenVideo = null },
            viewModel = viewModel
        )
    }
}
