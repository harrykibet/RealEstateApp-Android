package com.estatia.realestate.apps.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.designsystem.component.DynamicAsyncImage
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel


@Composable
private fun PropertyItem(
    modifier: Modifier = Modifier,
    property: Property,
    viewModel: VideoPlaybackViewModel = hiltViewModel(),
    isActive: Boolean, // 👈 important for state overlays
    onLikeClick: (Property) -> Unit,
    onCommentClick: (Property) -> Unit,
    onShareClick: (Property) -> Unit
) {
    // Collect player UI state only if active
    val uiState = if (isActive) {
        viewModel.uiState.collectAsState().value
    } else null

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // ------------------------
        // MEDIA
        // ------------------------
        if (property.videosAvailable && property.videoUrls.isNotEmpty()) {
            EngineVideoPlayer(
                mediaId = property.videoUrls.first(),
                mediaType = MediaType.VOD,
                modifier = Modifier.fillMaxSize(),
                viewModel = viewModel
            )
        } else {
            ImagePager(property.imageUrls)
        }

        // ------------------------
        // PLAYER UI OVERLAY
        // ------------------------
        uiState?.let { state ->
            when (state) {

                PlayerUiState.Buffering -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
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

                is PlayerUiState.Error -> {
                    Text(
                        text = state.message ?: "Playback error",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> Unit
            }
        }

        // ------------------------
        // GRADIENT OVERLAY
        // ------------------------
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        ),
                        startY = 500f
                    )
                )
        )

        // ------------------------
        // PROPERTY INFO
        // ------------------------
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
        ) {
            Text(
                text = property.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2
            )

            property.description?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 3
                )
            }
        }

        // ------------------------
        // ACTION BUTTONS
        // ------------------------
        FeedActionsColumn(
            property = property,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 24.dp)
        )
    }
}

@Composable
fun ImagePager(imageUrls: List<String>) {
    val pagerState = rememberPagerState { imageUrls.size }
    HorizontalPager(
        state =
            pagerState, modifier = Modifier.fillMaxSize()
    ) { page ->
        DynamicAsyncImage(
            imageUrl = imageUrls[page],
            contentDescription = "Property Image",
            modifier = Modifier.fillMaxSize()
        )
    }
}