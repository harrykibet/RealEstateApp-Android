package com.estatia.realestate.apps.feature.home.ui.screens

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.feature.home.ui.viewModels.playback.HomeVideoPlaybackViewModel
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedItem
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedScreen
import com.estatia.realestate.apps.feature.shared_ui.RememberFeedPlaybackCoordinator
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel
import androidx.media3.common.Player
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.localization.R as LocalizationR

@Composable
internal fun HomeRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
    playbackViewModel: HomeVideoPlaybackViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackUiState by playbackViewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
        commentsContent = commentsContent,
        playbackUiState = playbackUiState,
        onPlaybackRetry = playbackViewModel::retry,
        onPageVisible = playbackViewModel::onPageVisible,
        getPlayer = playbackViewModel::getPlayer,
        pausePlayback = playbackViewModel::pause,
        isMediaActive = playbackViewModel::isMediaActive,
        onLikeClick = { listing -> viewModel.toggleLike(listing.id, false) /* Fix: handle actual like state */ },
        onShareClick = { /* TODO */ },
        onRefresh = { viewModel.fetchProperties(isFirstLoad = true, pageSize = 20) },
    )
}

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    playbackUiState: PlayerUiState?,
    onPlaybackRetry: () -> Unit,
    onPageVisible: (FeedMediaContext) -> Unit,
    getPlayer: suspend (String, Uri, MediaType) -> Player,
    pausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
    onLikeClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onRefresh: () -> Unit,
) {
    val listings = remember(state.properties) {
        state.properties.map { it.toListingUiModel() }
    }

    HomeFeedContent(
        listings = listings,
        isLoading = state.isLoading,
        error = state.error,
        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
        commentsContent = commentsContent,
        playbackUiState = playbackUiState,
        onPlaybackRetry = onPlaybackRetry,
        onPageVisible = onPageVisible,
        getPlayer = getPlayer,
        pausePlayback = pausePlayback,
        isMediaActive = isMediaActive,
        onLikeClick = onLikeClick,
        onShareClick = onShareClick,
        onRefresh = onRefresh,
    )
}

@Composable
internal fun HomeFeedContent(
    listings: List<ListingUiModel>,
    isLoading: Boolean,
    error: String?,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    playbackUiState: PlayerUiState?,
    onPlaybackRetry: () -> Unit,
    onPageVisible: (FeedMediaContext) -> Unit,
    getPlayer: suspend (String, Uri, MediaType) -> Player,
    pausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
    onLikeClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onRefresh: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && listings.isEmpty()) {
            LoadingState(modifier = Modifier.align(Alignment.Center))
        } else if ((error != null) && listings.isEmpty()) {
            ErrorState(
                message = error,
                onRetry = onRefresh,
                modifier = Modifier.align(Alignment.Center),
            )
        } else if (listings.isEmpty()) {
            EmptyState(
                onRefresh = onRefresh,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            PropertyFeedScreen(
                listings = listings,
                playbackCoordinator = { pagerState, items ->
                    RememberFeedPlaybackCoordinator(
                        pagerState = pagerState,
                        items = items,
                        onPageVisible = onPageVisible
                    )
                },
                itemContent = { listing, isActive, onCommentClick ->
                    val currentPlayerUiState = if (isActive) playbackUiState else null

                    PropertyFeedItem(
                        listing = listing,
                        playerUiState = currentPlayerUiState,
                        onLikeClick = onLikeClick,
                        onCommentClick = { onCommentClick(it.id) },
                        onShareClick = onShareClick,
                        onRetry = onPlaybackRetry,
                        videoPlayerContent = {
                            val videoUrl = listing.videoUrl
                            if (videoUrl != null) {
                                EngineVideoPlayer(
                                    mediaId = videoUrl,
                                    uri = videoUrl.toUri(),
                                    mediaType = MediaType.VOD,
                                    getPlayer = getPlayer,
                                    onPause = pausePlayback,
                                    isActive = isMediaActive(videoUrl),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    )
                },
                commentsContent = commentsContent,
                onNavigateToDetails = onNavigateToPropertyDetail
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        EstatiaText(text = stringResource(LocalizationR.string.feature_home_loading), fontSize = 16.sp)
    }
}

@Composable
private fun EmptyState(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = EstatiaIcons.HomeBorder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline,
        )
        Spacer(Modifier.height(24.dp))
        EstatiaText(
            text = stringResource(LocalizationR.string.feature_home_no_properties_found),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        EstatiaText(
            text = stringResource(LocalizationR.string.feature_home_empty_description),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        EstatiaButton(onClick = onRefresh) {
            EstatiaText(stringResource(LocalizationR.string.feature_home_refresh))
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EstatiaText(
            text = stringResource(LocalizationR.string.feature_home_error_oops),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(16.dp))
        EstatiaText(
            text = message,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        EstatiaButton(onClick = onRetry) {
            EstatiaText(stringResource(LocalizationR.string.feature_home_retry))
        }
    }
}

@Preview(name = "Home - Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, widthDp = 400)
@DevicePreviews
@Composable
fun HomeLoadingPreview() {
    EstatiaTheme {
        EstatiaBackground {
            HomeFeedContent(
                listings = emptyList(),
                isLoading = true,
                error = null,
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                onPageVisible = {},
                getPlayer = { _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
            )
        }
    }
}

@Preview(name = "Home - Empty", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, widthDp = 400)
@DevicePreviews
@Composable
fun HomeEmptyPreview() {
    EstatiaTheme {
        EstatiaBackground {
            HomeFeedContent(
                listings = emptyList(),
                isLoading = false,
                error = null,
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                onPageVisible = {},
                getPlayer = { _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
            )
        }
    }
}

@Preview(name = "Home - Error", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, widthDp = 400)
@DevicePreviews
@Composable
fun HomeErrorPreview() {
    EstatiaTheme {
        EstatiaBackground {
            HomeFeedContent(
                listings = emptyList(),
                isLoading = false,
                error = "Connection timeout. Please check your internet.",
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                onPageVisible = {},
                getPlayer = { _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
            )
        }
    }
}

@Preview(name = "Home - Content", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, widthDp = 400)
@DevicePreviews
@Composable
fun HomeContentPreview() {
    EstatiaTheme {
        EstatiaBackground {
            HomeFeedContent(
                listings = listOf(
                    ListingUiModel(
                        id = "1",
                        title = "Modern Apartment",
                        description = "Luxury living in the heart of the city.",
                        price = 15000000.0,
                        videoUrl = null,
                        ownerName = "jane_doe",
                        ownerAvatarUrl = null,
                        likesCount = 120,
                        commentsCount = 45,
                        sharesCount = 12
                    )
                ),
                isLoading = false,
                error = null,
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                onPageVisible = {},
                getPlayer = { _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
            )
        }
    }
}

@Preview(name = "Home - Content (Swahili)", showBackground = true, locale = "sw", widthDp = 400)
@Composable
fun HomeContentSwahiliPreview() {
    EstatiaTheme {
        EstatiaBackground {
            HomeFeedContent(
                listings = listOf(
                    ListingUiModel(
                        id = "1",
                        title = "Apartment ya Kisasa",
                        description = "Maisha ya kifahari katikati ya jiji.",
                        price = 15000000.0,
                        videoUrl = null,
                        ownerName = "jane_doe",
                        ownerAvatarUrl = null,
                        likesCount = 120,
                        commentsCount = 45,
                        sharesCount = 12
                    )
                ),
                isLoading = false,
                error = null,
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackUiState = PlayerUiState.Idle,
                onPlaybackRetry = {},
                onPageVisible = {},
                getPlayer = { _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
            )
        }
    }
}
