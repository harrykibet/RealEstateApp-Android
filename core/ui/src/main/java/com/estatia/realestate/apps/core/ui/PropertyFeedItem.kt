package com.estatia.realestate.apps.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel


@Composable
fun PropertyFeedItem(
    property: Property,
    viewModel: VideoPlaybackViewModel,
    onLikeClick: (Property) -> Unit,
    onCommentClick: (Property) -> Unit,
    onShareClick: (Property) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        EngineVideoPlayer(
            mediaId = property.videoUrls.first(),
            mediaType = MediaType.VOD,
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel
        )

        // Optional bottom gradient for readability
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

        // Left bottom → property info
        PropertyInfoOverlay(
            property = property,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
        )

        // Right bottom → action buttons
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