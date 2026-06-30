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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel


@Composable
fun PropertyFeedItem(
    property: PropertyDomainModel,
    viewModel: VideoPlaybackViewModel,
    isActive: Boolean,
    onLikeClick: (PropertyDomainModel) -> Unit,
    onCommentClick: (PropertyDomainModel) -> Unit,
    onShareClick: (PropertyDomainModel) -> Unit
) {
    val uiState = if (isActive) { viewModel.uiState.collectAsState().value } else { null }

    Box(modifier = Modifier.fillMaxSize()) {

        EngineVideoPlayer(
            mediaId = property.videoUrls.first(),
            mediaType = MediaType.VOD,
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel
        )

        // ---------------------------------------
        // Playback UI Overlay (ONLY if active)
        // ---------------------------------------

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
            property = property,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
        )

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