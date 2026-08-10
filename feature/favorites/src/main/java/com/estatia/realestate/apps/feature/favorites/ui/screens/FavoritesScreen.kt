package com.estatia.realestate.apps.feature.favorites.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedItem
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedScreen
import com.estatia.realestate.apps.feature.shared_ui.RememberFeedPlaybackCoordinator
import com.estatia.realestate.apps.feature.favorites.ui.viewmodels.playback.FavoritesVideoPlaybackViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavoritesRoute(
    onPropertyClick: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    playbackViewModel: FavoritesVideoPlaybackViewModel = hiltViewModel()
) {
    FavoritesScreen(
        favoriteProperties = emptyList(), // TODO: Get from ViewModel
        onLikeClick = {},
        onShareClick = {},
        onPropertyClick = onPropertyClick,
        commentsContent = commentsContent,
        playbackViewModel = playbackViewModel
    )
}

@Composable
fun FavoritesScreen(
    favoriteProperties: List<PropertyDomainModel>,
    onLikeClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onPropertyClick: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    playbackViewModel: FavoritesVideoPlaybackViewModel
) {
    val listings = remember(favoriteProperties) {
        favoriteProperties.map { it.toListingUiModel() }
    }

    PropertyFeedScreen(
        listings = listings,
        playbackCoordinator = { pagerState, items ->
            RememberFeedPlaybackCoordinator(
                pagerState = pagerState,
                items = items,
                onPageVisible = playbackViewModel::onPageVisible
            )
        },
        itemContent = { listing, isActive, onCommentClick ->
            val playerUiState by if (isActive) {
                playbackViewModel.uiState.collectAsStateWithLifecycle()
            } else {
                remember { mutableStateOf(null) }
            }

            PropertyFeedItem(
                listing = listing,
                playerUiState = playerUiState,
                onLikeClick = { onLikeClick(it.id) },
                onCommentClick = { onCommentClick(it.id) },
                onShareClick = { onShareClick(it.id) },
                onRetry = { playbackViewModel.retry() },
                videoPlayerContent = {
                    val videoUrl = listing.videoUrl
                    if (videoUrl != null) {
                        EngineVideoPlayer(
                            mediaId = videoUrl,
                            uri = videoUrl.toUri(),
                            mediaType = MediaType.VOD,
                            getPlayer = playbackViewModel::getPlayer,
                            onPause = { playbackViewModel.pause() },
                            isActive = playbackViewModel.isMediaActive(videoUrl),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                onClick = { onPropertyClick(it.id) }
            )
        },
        commentsContent = commentsContent,
        onNavigateToDetails = onPropertyClick
    )
}
