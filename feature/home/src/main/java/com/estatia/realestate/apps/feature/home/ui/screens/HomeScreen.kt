package com.estatia.realestate.apps.feature.home.ui.screens

import android.content.res.Configuration
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.estatia.realestate.apps.core.model.common.MediaReference
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
import com.estatia.realestate.apps.core.model.player.FeedNeighbor
import com.estatia.realestate.apps.core.localization.R as LocalizationR

@Composable
internal fun HomeRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String, onDismiss: () -> Unit) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
    playbackViewModel: HomeVideoPlaybackViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackUiState by playbackViewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by playbackViewModel.isMuted.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(playbackViewModel.autoAdvanceEvent) {
        playbackViewModel.autoAdvanceEvent.collect {
            snackbarHostState.showSnackbar(
                message = "Skipping video due to slow connection...",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }

    DisposableEffect(playbackViewModel) {
        playbackViewModel.onScreenVisible()
        onDispose {
            playbackViewModel.onScreenHidden()
        }
    }

    LaunchedEffect(playbackViewModel.meteredConnectionEvent) {
        playbackViewModel.meteredConnectionEvent.collect {
            snackbarHostState.showSnackbar(
                message = "You're now using mobile data. Video quality may adjust to save data.",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        HomeScreen(
            state = state,
            onNavigateToPropertyDetail = onNavigateToPropertyDetail,
            commentsContent = commentsContent,
            playbackUiState = playbackUiState,
            isMuted = isMuted,
            onMuteToggle = playbackViewModel::toggleMute,
            onPlaybackRetry = playbackViewModel::retry,
            onPageVisible = { id, uri, match, prev, next, title, artist ->
                playbackViewModel.onPageVisible(id, uri, match, prev, next, title, artist)
            },
            getPlayer = { id, uri, type, score -> playbackViewModel.getPlayer(id, MediaReference(uri.toString()), type, score) },
            pausePlayback = playbackViewModel::pause,
            isMediaActive = playbackViewModel::isMediaActive,
            onLikeClick = { listing -> viewModel.toggleLike(listing.id, false) /* Fix: handle actual like state */ },
            onShareClick = { /* TODO */ },
            onRefresh = { viewModel.fetchProperties(isFirstLoad = true, pageSize = 20) },
            onPageChanged = viewModel::onPageChanged,
            autoAdvanceEvent = playbackViewModel.autoAdvanceEvent,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String, onDismiss: () -> Unit) -> Unit,
    playbackUiState: PlayerUiState?,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onPlaybackRetry: () -> Unit,
    onPageVisible: (String, MediaReference, Float, List<FeedNeighbor>, List<FeedNeighbor>, String?, String?) -> Unit,
    getPlayer: suspend (String, android.net.Uri, MediaType, Float) -> Player,
    pausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
    onLikeClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onRefresh: () -> Unit,
    onPageChanged: (Int) -> Unit,
    autoAdvanceEvent: kotlinx.coroutines.flow.Flow<Unit>? = null,
    onAutoAdvanceAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val listings = remember(state.properties) {
        state.properties.map { it.toListingUiModel() }
    }

    HomeFeedContent(
        listings = listings,
        isLoading = state.isLoading,
        error = state.error,
        initialPage = state.initialPage,
        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
        commentsContent = commentsContent,
        playbackUiState = playbackUiState,
        isMuted = isMuted,
        onMuteToggle = onMuteToggle,
        onPlaybackRetry = onPlaybackRetry,
        onPageVisible = onPageVisible,
        getPlayer = getPlayer,
        pausePlayback = pausePlayback,
        isMediaActive = isMediaActive,
        onLikeClick = onLikeClick,
        onShareClick = onShareClick,
        onRefresh = onRefresh,
        onPageChanged = onPageChanged,
        autoAdvanceEvent = autoAdvanceEvent,
        onAutoAdvanceAction = onAutoAdvanceAction,
        modifier = modifier
    )
}

@Composable
internal fun HomeFeedContent(
    listings: List<ListingUiModel>,
    isLoading: Boolean,
    error: String?,
    initialPage: Int,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String, onDismiss: () -> Unit) -> Unit,
    playbackUiState: PlayerUiState?,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    onPlaybackRetry: () -> Unit,
    onPageVisible: (String, MediaReference, Float, List<FeedNeighbor>, List<FeedNeighbor>, String?, String?) -> Unit,
    getPlayer: suspend (String, android.net.Uri, MediaType, Float) -> Player,
    pausePlayback: () -> Unit,
    isMediaActive: (String) -> Boolean,
    onLikeClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onRefresh: () -> Unit,
    onPageChanged: (Int) -> Unit,
    autoAdvanceEvent: kotlinx.coroutines.flow.Flow<Unit>? = null,
    onAutoAdvanceAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
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
                initialPage = initialPage,
                onPageChanged = onPageChanged,
                autoAdvanceEvent = autoAdvanceEvent,
                onAutoAdvanceAction = onAutoAdvanceAction,
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
                            val videoUriStr = (listing.hlsUrl ?: listing.directVideoUrl)
                            if (videoUriStr != null) {
                                val videoUri = videoUriStr.toUri()
                                EngineVideoPlayer(
                                    mediaId = listing.id,
                                    uri = videoUri,
                                    mediaType = MediaType.VOD,
                                    matchScore = listing.matchScore,
                                    getPlayer = { id, uri, type, score -> getPlayer(id, uri, type, score) },
                                    onPause = pausePlayback,
                                    isActive = isMediaActive(listing.id),
                                    isMuted = isMuted,
                                    onMuteToggle = onMuteToggle,
                                    onLike = { onLikeClick(listing) },
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
                initialPage = 0,
                onNavigateToPropertyDetail = {},
                commentsContent = { _, _ -> },
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                onPageVisible = { _, _, _, _, _, _, _ -> },
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
                onPageChanged = {},
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
                initialPage = 0,
                onNavigateToPropertyDetail = {},
                commentsContent = { _, _ -> },
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                onPageVisible = { _, _, _, _, _, _, _ -> },
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
                onPageChanged = {},
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
                initialPage = 0,
                onNavigateToPropertyDetail = {},
                commentsContent = { _, _ -> },
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                onPageVisible = { _, _, _, _, _, _, _ -> },
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
                onPageChanged = {},
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
                        directVideoUrl = null,
                        hlsUrl = null,
                        ownerName = "jane_doe",
                        ownerAvatarUrl = null,
                        likesCount = 120,
                        commentsCount = 45,
                        sharesCount = 12,
                        matchScore = 0.9f
                    )
                ),
                isLoading = false,
                error = null,
                initialPage = 0,
                onNavigateToPropertyDetail = {},
                commentsContent = { _, _ -> },
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                onPageVisible = { _, _, _, _, _, _, _ -> },
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
                onPageChanged = {},
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
                        directVideoUrl = null,
                        hlsUrl = null,
                        ownerName = "jane_doe",
                        ownerAvatarUrl = null,
                        likesCount = 120,
                        commentsCount = 45,
                        sharesCount = 12,
                        matchScore = 0.9f
                    )
                ),
                isLoading = false,
                error = null,
                initialPage = 0,
                onNavigateToPropertyDetail = {},
                commentsContent = { _, _ -> },
                playbackUiState = PlayerUiState.Idle,
                isMuted = false,
                onMuteToggle = {},
                onPlaybackRetry = {},
                onPageVisible = { _, _, _, _, _, _, _ -> },
                getPlayer = { _, _, _, _ -> throw Exception("Not implemented") },
                pausePlayback = {},
                isMediaActive = { false },
                onLikeClick = {},
                onShareClick = {},
                onRefresh = {},
                onPageChanged = {},
            )
        }
    }
}
