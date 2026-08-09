package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.player_ui.state.FeedMediaContext
import com.estatia.realestate.apps.core.player_ui.state.FeedNeighbor

@Composable
fun RememberFeedPlaybackCoordinator(
    pagerState: PagerState,
    items: List<ListingUiModel>,
    onPageVisible: (FeedMediaContext) -> Unit
) {
    LaunchedEffect(pagerState.currentPage) {

        val page = pagerState.currentPage
        val current = items.getOrNull(page) ?: return@LaunchedEffect

        val currentVideoUrl = current.videoUrl ?: return@LaunchedEffect

        val previous = items.getOrNull(page - 1)?.let {
            val videoUrl = it.videoUrl
            if (videoUrl != null) {
                FeedNeighbor(
                    mediaId = it.id,
                    uri = videoUrl.toUri()
                )
            } else null
        }

        val next = items.getOrNull(page + 1)?.let {
            val videoUrl = it.videoUrl
            if (videoUrl != null) {
                FeedNeighbor(
                    mediaId = it.id,
                    uri = videoUrl.toUri()
                )
            } else null
        }

        onPageVisible(
            FeedMediaContext(
                mediaId = current.id,
                uri = currentVideoUrl.toUri(),
                previous = previous,
                next = next
            )
        )
    }
}
