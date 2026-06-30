package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.runtime.*
import androidx.core.net.toUri
import androidx.compose.foundation.pager.PagerState
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.player_ui.state.FeedNeighbor
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun RememberFeedPlaybackCoordinator(
    pagerState: PagerState,
    items: List<PropertyDomainModel>,
    viewModel: VideoPlaybackViewModel
) {
    LaunchedEffect(pagerState.currentPage) {

        val page = pagerState.currentPage
        val current = items.getOrNull(page) ?: return@LaunchedEffect

        val previous = items.getOrNull(page - 1)?.let {
            FeedNeighbor(
                mediaId = it.videoUrls.first(),
                uri = it.videoUrls.first().toUri()
            )
        }

        val next = items.getOrNull(page + 1)?.let {
            FeedNeighbor(
                mediaId = it.videoUrls.first(),
                uri = it.videoUrls.first().toUri()
            )
        }

        viewModel.onPageVisible(
            FeedMediaContext(
                mediaId = current.videoUrls.first(),
                uri = current.videoUrls.first().toUri(),
                previous = previous,
                next = next
            )
        )
    }
}