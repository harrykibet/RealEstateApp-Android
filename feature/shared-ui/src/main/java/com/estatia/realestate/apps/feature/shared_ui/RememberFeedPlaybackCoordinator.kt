package com.estatia.realestate.apps.feature.shared_ui

import android.net.Uri
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
import com.estatia.realestate.apps.core.model.property.ListingUiModel
import com.estatia.realestate.apps.core.model.player.FeedNeighbor

@Composable
fun RememberFeedPlaybackCoordinator(
    pagerState: PagerState,
    items: List<ListingUiModel>,
    onPageVisible: (String, Uri, Float, List<FeedNeighbor>, List<FeedNeighbor>, String?, String?) -> Unit
) {
    LaunchedEffect(pagerState.currentPage) {

        val page = pagerState.currentPage
        val current = items.getOrNull(page) ?: return@LaunchedEffect

        val currentUri = (current.hlsUrl ?: current.videoUrl)?.toUri() ?: return@LaunchedEffect

        // Collect Previous Neighbors (N=1)
        val previous = mutableListOf<FeedNeighbor>()
        items.getOrNull(page - 1)?.let {
            val uri = (it.hlsUrl ?: it.videoUrl)?.toUri()
            if (uri != null) {
                previous.add(FeedNeighbor(
                    mediaId = it.id,
                    uri = uri,
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
                val uri = (it.hlsUrl ?: it.videoUrl)?.toUri()
                if (uri != null) {
                    next.add(FeedNeighbor(
                        mediaId = it.id,
                        uri = uri,
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
