package com.estatia.realestate.apps.core.player_ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController

@Composable
fun EngineVideoFeedWithFullScreen(
    videos: List<VideoItem>,
    controller: ISharedPlayerController
) {
    var fullScreenVideo by remember { mutableStateOf<VideoItem?>(null) }

    EngineVideoFeed(
        videos = videos,
        controller = controller,
        modifier = Modifier.fillMaxSize()
    ) { clickedVideo ->
        // Trigger full-screen
        fullScreenVideo = clickedVideo
    }

    fullScreenVideo?.let { video ->
        EngineVideoFullScreen(
            mediaId = video.mediaId,
            mediaType = MediaType.VOD,
            controller = controller,
            onExitFullScreen = { fullScreenVideo = null }
        )
    }
}
