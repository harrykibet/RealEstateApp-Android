package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun PropertyFeedScreen(
    properties: List<PropertyDomainModel>,
    viewModel: VideoPlaybackViewModel = hiltViewModel(),
    onLikeClick: (PropertyDomainModel) -> Unit,
    onCommentClick: (PropertyDomainModel) -> Unit,
    onShareClick: (PropertyDomainModel) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { properties.size }
    )

    RememberFeedPlaybackCoordinator(
        pagerState = pagerState,
        items = properties,
        viewModel = viewModel
    )

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        val property = properties[page]

        PropertyFeedItem(
            property = property,
            viewModel = viewModel,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            isActive = pagerState.currentPage == page
        )
    }
}