package com.estatia.realestate.apps.feature.shared_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.DynamicAsyncImage
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState


@Composable
fun PropertyItem(
    modifier: Modifier = Modifier,
    listing: ListingUiModel,
    imageUrls: List<String> = emptyList(), // Images are extra for full item
    playerUiState: PlayerUiState?,
    onLikeClick: (ListingUiModel) -> Unit,
    onCommentClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    videoPlayerContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {

        // ------------------------
        // MEDIA
        // ------------------------
        val directVideoUrl = listing.directVideoUrl
        if (directVideoUrl != null) {
            videoPlayerContent()
        } else {
            ImagePager(imageUrls)
        }

        // ------------------------
        // PLAYER UI OVERLAY
        // ------------------------
        playerUiState?.let { state ->
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
                    EstatiaText(
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
        PropertyInfoOverlay(
            listing = listing,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
        )

        // ------------------------
        // ACTION BUTTONS
        // ------------------------
        FeedActionsColumn(
            listing = listing,
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
