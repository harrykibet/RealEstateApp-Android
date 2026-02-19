package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.feature.VideoItem
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun EngineVideoFeed(
    videos: List<VideoItem>,
    modifier: Modifier = Modifier,
    viewModel: VideoPlaybackViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { videos.size })

    var fullScreenVideo by remember { mutableStateOf<VideoItem?>(null) }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        val video = videos[page]

        // Autoplay current & preload adjacent
        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == page) {
                val prevId = videos.getOrNull(page - 1)?.mediaId
                val nextId = videos.getOrNull(page + 1)?.mediaId

                viewModel.onPageVisible(
                    mediaId = video.mediaId,
                    mediaType = MediaType.VOD,
                    previousMediaId = prevId,
                    nextMediaId = nextId
                )
            } else {
                viewModel.pause()
            }
        }

        EngineVideoPlayer(
            mediaId = video.mediaId,
            mediaType = MediaType.VOD,
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            onClick = { fullScreenVideo = video }
        )
    }

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
