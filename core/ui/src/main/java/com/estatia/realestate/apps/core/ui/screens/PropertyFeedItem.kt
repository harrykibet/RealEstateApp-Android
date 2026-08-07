package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.player_ui.screens.PlaybackErrorView
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState

@Composable
fun PropertyFeedItem(
    listing: ListingUiModel,
    playerUiState: PlayerUiState?,
    onLikeClick: (ListingUiModel) -> Unit,
    onCommentClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onRetry: () -> Unit,
    videoPlayerContent: @Composable () -> Unit,
    onClick: (ListingUiModel) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        videoPlayerContent()

        // ---------------------------------------
        // Playback UI Overlay (ONLY if active)
        // ---------------------------------------

        playerUiState?.let { state ->
            when (state) {

                PlayerUiState.Buffering -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
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

        // ---------------------------------------
        // Gradient
        // ---------------------------------------

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // ---------------------------------------
        // Overlays
        // ---------------------------------------

        PropertyInfoOverlay(
            listing = listing,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
        )

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
