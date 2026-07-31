package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun PropertyFeedScreen(
    listings: List<ListingUiModel>,
    viewModel: VideoPlaybackViewModel? = if (LocalInspectionMode.current) null else hiltViewModel(),
    onLikeClick: (ListingUiModel) -> Unit,
    onCommentClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    onPropertyClick: (ListingUiModel) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { listings.size }
    )

    if (viewModel != null) {
        RememberFeedPlaybackCoordinator(
            pagerState = pagerState,
            items = listings,
            viewModel = viewModel
        )
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        val listing = listings[page]

        PropertyFeedItem(
            listing = listing,
            viewModel = viewModel,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            isActive = pagerState.currentPage == page,
            onClick = onPropertyClick
        )
    }
}
