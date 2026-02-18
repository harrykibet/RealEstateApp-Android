package com.estatia.realestate.apps.core.player_ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.core.domain.model.VideoItem
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.player_engine.core.ISharedPlayerController

@Composable
fun EngineVideoFeed(
    videos: List<VideoItem>,
    controller: ISharedPlayerController,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    VerticalPager(
        count = videos.size,
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val video = videos[page]

        // Autoplay when this page is visible
        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == page) {
                controller.preload(video.mediaId, MediaType.VOD) // preload ahead
                controller.play(video.mediaId, MediaType.VOD)
            } else {
                controller.pause()
            }
        }

        EngineVideoPlayer(
            mediaId = video.mediaId,
            mediaType = MediaType.VOD,
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Optional: preloading next video for smooth scrolling
    LaunchedEffect(pagerState.currentPage) {
        val nextIndex = pagerState.currentPage + 1
        if (nextIndex < videos.size) {
            val nextVideo = videos[nextIndex]
            controller.preload(nextVideo.mediaId, MediaType.VOD)
        }
    }
}
