package com.estatia.realestate.apps.feature.shared_ui

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.model.player.FeedNeighbor

@Composable
fun RememberFeedPlaybackCoordinator(
    pagerState: PagerState,
    items: List<ListingUiModel>,
    onPageVisible: (String, MediaReference, Float, List<FeedNeighbor>, List<FeedNeighbor>, String?, String?) -> Unit
) {
    LaunchedEffect(pagerState.currentPage) {

        val page = pagerState.currentPage
        val current = items.getOrNull(page) ?: return@LaunchedEffect

        val currentUriStr = (current.hlsUrl ?: current.directVideoUrl) ?: return@LaunchedEffect
        val currentUri = MediaReference(currentUriStr)

        // Collect Previous Neighbors (N=1)
        val previous = mutableListOf<FeedNeighbor>()
        items.getOrNull(page - 1)?.let {
            val uriStr = (it.hlsUrl ?: it.directVideoUrl)
            if (uriStr != null) {
                previous.add(FeedNeighbor(
                    mediaId = it.id,
                    uri = MediaReference(uriStr),
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
                val uriStr = (it.hlsUrl ?: it.directVideoUrl)
                if (uriStr != null) {
                    next.add(FeedNeighbor(
                        mediaId = it.id,
                        uri = MediaReference(uriStr),
                        matchScore = it.matchScore,
                        title = it.title,
                        artist = it.ownerName
                    ))
                }
            }
        }

        onPageVisible(
            current.id,
            currentUri,
            current.matchScore,
            previous,
            next,
            current.title,
            current.ownerName
        )
    }
}
