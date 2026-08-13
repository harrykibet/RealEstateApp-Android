package com.estatia.realestate.apps.feature.shared_ui

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

        // Collect Previous Neighbors (N=1)
        val previous = mutableListOf<FeedNeighbor>()
        items.getOrNull(page - 1)?.let {
            it.videoUrl?.let { url ->
                previous.add(FeedNeighbor(
                    mediaId = it.id,
                    uri = url.toUri(),
                    matchScore = it.matchScore,
                    title = it.title,
                    artist = it.ownerName
                ))
            }
        }

        // Collect Next Neighbors (N=2)
        val next = mutableListOf<FeedNeighbor>()
        for (i in 1..2) {
            items.getOrNull(page + i)?.let {
                it.videoUrl?.let { url ->
                    next.add(FeedNeighbor(
                        mediaId = it.id,
                        uri = url.toUri(),
                        matchScore = it.matchScore,
                        title = it.title,
                        artist = it.ownerName
                    ))
                }
            }
        }

        onPageVisible(
            FeedMediaContext(
                mediaId = current.id,
                uri = currentVideoUrl.toUri(),
                matchScore = current.matchScore,
                title = current.title,
                artist = current.ownerName,
                previous = previous,
                next = next
            )
        )
    }
}
