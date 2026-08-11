package com.estatia.realestate.apps.core.player_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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

/**
 * A specialized vertical feed component designed for high-performance video playback.
 * Integrates with [VideoPlaybackCoordinator] to handle prefetching and player recycling.
 * 
 * @param videos List of video items to display.
 * @param uiState The current state of the active player (from [BaseVideoPlaybackViewModel]).
 * @param onPageVisible Callback triggered when a new page becomes visible, used to initiate playback and prewarm neighbors.
 * @param onRetry Callback for retrying playback after an error.
 * @param videoPlayerContent Composable lambda for rendering the individual video player.
 */
@Composable
fun EngineVideoFeed(
    videos: List<VideoItem>,
    uiState: PlayerUiState,
    onPageVisible: (FeedMediaContext) -> Unit,
    onRetry: () -> Unit,
    videoPlayerContent: @Composable (VideoItem, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { videos.size })
    val currentOnPageVisible by rememberUpdatedState(onPageVisible)

    var lastDispatchedPage by remember { mutableIntStateOf(-1) }

    LaunchedEffect(pagerState, videos.size) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page == lastDispatchedPage) return@collect
                lastDispatchedPage = page

                val video = videos.getOrNull(page) ?: return@collect
                val previous = videos.getOrNull(page - 1)?.let {
                    listOf(FeedNeighbor(
                        mediaId = it.mediaId,
                        uri = it.videoUrl.toUri()
                    ))
                } ?: emptyList()
                
                val next = mutableListOf<FeedNeighbor>()
                for (i in 1..2) {
                    videos.getOrNull(page + i)?.let {
                        next.add(FeedNeighbor(
                            mediaId = it.mediaId,
                            uri = it.videoUrl.toUri()
                        ))
                    }
                }

                currentOnPageVisible(
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

                    PlayerUiState.Reconnecting -> {
                        PlaybackReconnectingView(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is PlayerUiState.Error -> {
                        PlaybackErrorView(
                            errorState = state,
                            onRetry = onRetry,
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
