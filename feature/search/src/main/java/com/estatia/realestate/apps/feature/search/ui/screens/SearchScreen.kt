package com.estatia.realestate.apps.feature.search.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.player_ui.screens.EngineVideoPlayer
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedItem
import com.estatia.realestate.apps.feature.shared_ui.PropertyFeedScreen
import com.estatia.realestate.apps.feature.shared_ui.RememberFeedPlaybackCoordinator
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel
import com.estatia.realestate.apps.feature.search.ui.viewmodels.playback.SearchVideoPlaybackViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    playbackViewModel: SearchVideoPlaybackViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

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

    SearchScreen(
        uiState = uiState,
        onSearch = viewModel::searchProperties,
        onClearHistory = viewModel::clearSearchHistory,
        onLikeClick = { listing -> viewModel.toggleLike(listing.id, false) /* Fix: handle actual state */ },
        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
        commentsContent = commentsContent,
        playbackViewModel = playbackViewModel,
        onPageChanged = viewModel::onPageChanged,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onSearch: (String) -> Unit,
    onClearHistory: () -> Unit,
    onLikeClick: (ListingUiModel) -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    commentsContent: @Composable (propertyId: String) -> Unit,
    playbackViewModel: SearchVideoPlaybackViewModel,
    onPageChanged: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                EstatiaTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search properties...",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is SearchUiState.Initial, SearchUiState.Loading -> {
                    if (uiState is SearchUiState.Loading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is SearchUiState.History -> {
                    SearchHistorySection(
                        history = uiState.history,
                        onHistoryItemClick = {
                            searchQuery = it
                            onSearch(it)
                        },
                        onClearHistory = onClearHistory
                    )
                }

                is SearchUiState.Success -> {
                    val listings = remember(uiState.results) {
                        uiState.results.map { it.toListingUiModel() }
                    }

                    if (listings.isEmpty()) {
                        EmptySearchResults()
                    } else {
                        PropertyFeedScreen(
                            listings = listings,
                            initialPage = uiState.initialPage,
                            onPageChanged = onPageChanged,
                            playbackCoordinator = { pagerState, items ->
                                RememberFeedPlaybackCoordinator(
                                    pagerState = pagerState,
                                    items = items,
                                    onPageVisible = { id, uri, match, prev, next, title, artist ->
                                        playbackViewModel.onPageVisible(id, uri, match, prev, next, title, artist)
                                    }
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
                                    onLikeClick = onLikeClick,
                                    onCommentClick = { onCommentClick(it.id) },
                                    onShareClick = {},
                                    onRetry = { playbackViewModel.retry() },
                                    videoPlayerContent = {
                                        val videoUri = (listing.hlsUrl ?: listing.videoUrl)?.toUri()
                                        if (videoUri != null) {
                                            EngineVideoPlayer(
                                                mediaId = listing.id,
                                                uri = videoUri,
                                                mediaType = MediaType.VOD,
                                                matchScore = listing.matchScore,
                                                getPlayer = { id, uri, type, score -> playbackViewModel.getPlayer(id, uri, type, score) },
                                                onPause = { playbackViewModel.pause() },
                                                isActive = playbackViewModel.isMediaActive(listing.id),
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

                is SearchUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EstatiaText(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    // Auto-search if query is typed and enter is pressed (simplified for now)
    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            onSearch(searchQuery)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchHistorySection(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EstatiaText(
                text = "Recent Searches",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = onClearHistory) {
                EstatiaText(text = "Clear All", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            EstatiaText(
                text = "No recent searches",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                history.forEach { item ->
                    SuggestionChip(
                        onClick = { onHistoryItemClick(item) },
                        label = { EstatiaText(text = item) },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchResults() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = EstatiaIcons.SearchBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        EstatiaText(
            text = "No properties found",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        EstatiaText(
            text = "Try searching for a different location or property type.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@DevicePreviews
@Composable
fun SearchScreenHistoryPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SearchScreen(
                uiState = SearchUiState.History(listOf("Nairobi", "Apartment", "Westlands")),
                onSearch = {},
                onClearHistory = {},
                onLikeClick = {},
                onNavigateToPropertyDetail = {},
                commentsContent = {},
                playbackViewModel = hiltViewModel(),
                onPageChanged = {},
                snackbarHostState = remember { SnackbarHostState() }
            )
        }
    }
}
