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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedItem
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel

@Composable
internal fun HomeRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        state = state,
        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
        onCommentClick = onCommentClick,
        onRefresh = { viewModel.fetchProperties(isFirstLoad = true, pageSize = 20) },
    )
}

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onNavigateToPropertyDetail: (String) -> Unit,
    onCommentClick: (String) -> Unit,
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
        onCommentClick = onCommentClick,
        onRefresh = onRefresh
    )
}

@Composable
internal fun HomeFeedContent(
    listings: List<ListingUiModel>,
    isLoading: Boolean,
    error: String?,
    onNavigateToPropertyDetail: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && listings.isEmpty()) {
            LoadingState(modifier = Modifier.align(Alignment.Center))
        } else if (error != null && listings.isEmpty()) {
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
                onLikeClick = { /* TODO */ },
                onCommentClick = { listing -> onCommentClick(listing.id) },
                onShareClick = { /* TODO */ },
                onPropertyClick = { listing -> onNavigateToPropertyDetail(listing.id) }
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
        EstatiaText(text = "Loading properties...", fontSize = 16.sp)
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
            text = "No properties found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        EstatiaText(
            text = "Check back later or try refreshing to see new listings.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        EstatiaButton(onClick = onRefresh) {
            EstatiaText("Refresh")
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
            text = "Oops!",
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
            EstatiaText("Retry")
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
                onCommentClick = {},
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
                onCommentClick = {},
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
                onCommentClick = {},
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
                        videoUrl = null,
                        likesCount = 120,
                        commentsCount = 45,
                        sharesCount = 12
                    )
                ),
                isLoading = false,
                error = null,
                onNavigateToPropertyDetail = {},
                onCommentClick = {},
                onRefresh = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ListingItemPreview() {
    EstatiaTheme {
        PropertyFeedItem(
            listing = ListingUiModel(
                id = "1",
                title = "Modern Apartment",
                description = "Luxury living in the heart of the city.",
                videoUrl = null,
                likesCount = 120,
                commentsCount = 45,
                sharesCount = 12
            ),
            viewModel = null,
            isActive = true,
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {},
            onClick = {}
        )
    }
}
