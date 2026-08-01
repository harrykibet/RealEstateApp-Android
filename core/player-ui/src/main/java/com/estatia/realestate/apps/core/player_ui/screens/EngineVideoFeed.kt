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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.model.feature.VideoItem
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.player_ui.state.FeedNeighbor
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun EngineVideoFeed(
    videos: List<VideoItem>,
    uiState: PlayerUiState,
    onPageVisible: (FeedMediaContext) -> Unit,
    videoPlayerContent: @Composable (VideoItem, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = { videos.size })

    // Prevent duplicate dispatches
    var lastDispatchedPage by remember { mutableIntStateOf(-1) }

    LaunchedEffect(pagerState) {

        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->

                // HARD GUARD: avoid duplicate calls
                if (page == lastDispatchedPage) return@collect
                lastDispatchedPage = page

                val video = videos.getOrNull(page) ?: return@collect

                val previous = videos.getOrNull(page - 1)?.let {
                    FeedNeighbor(
                        mediaId = it.mediaId,
                        uri = it.videoUrl.toUri()
                    )
                }

                val next = videos.getOrNull(page + 1)?.let {
                    FeedNeighbor(
                        mediaId = it.mediaId,
                        uri = it.videoUrl.toUri()
                    )
                }

                onPageVisible(
                    FeedMediaContext(
                        mediaId = video.mediaId,
                        uri = video.videoUrl.toUri(),
                        previous = previous,
                        next = next
                    )
                )
            }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        val video = videos[page]
        val isActive = pagerState.currentPage == page

        Box(modifier = Modifier.fillMaxSize()) {

            videoPlayerContent(video, isActive)

            if (isActive) {
                when (val state = uiState) {

                    PlayerUiState.Buffering -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    is PlayerUiState.Error -> {
                        EstatiaText(
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
