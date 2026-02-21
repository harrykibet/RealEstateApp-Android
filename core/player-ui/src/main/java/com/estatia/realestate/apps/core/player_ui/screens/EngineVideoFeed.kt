package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.feature.VideoItem
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun EngineVideoFeed(
    videos: List<VideoItem>,
    modifier: Modifier = Modifier,
    viewModel: VideoPlaybackViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { videos.size })

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val video = videos[page]
        val isActive = pagerState.currentPage == page

        // Only collect state for active page
        val uiState = if (isActive) {
            viewModel.uiState.collectAsState().value
        } else {
            null
        }

        // Autoplay current & preload adjacent
        LaunchedEffect(pagerState.currentPage) {
            if (isActive) {
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

        Box(modifier = Modifier.fillMaxSize()) {

            EngineVideoPlayer(
                mediaId = video.mediaId,
                mediaType = MediaType.VOD,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )

            // ----------------------------
            // Playback UI Overlay
            // ----------------------------

            uiState?.let { state ->
                when (state) {

                    PlayerUiState.Buffering -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    is PlayerUiState.Error -> {
                        Text(
                            text = state.message ?: "Playback error",
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    PlayerUiState.Paused -> {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(72.dp)
                                .align(Alignment.Center)
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}