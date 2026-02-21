package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.domain.interfaces.MediaType
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.core.player_ui.viewModels.VideoPlaybackViewModel

@Composable
fun PropertyFeedScreen(
    properties: List<Property>,
    viewModel: VideoPlaybackViewModel = hiltViewModel(),
    onLikeClick: (Property) -> Unit,
    onCommentClick: (Property) -> Unit,
    onShareClick: (Property) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { properties.size }
    )

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->

        val property = properties[page]

        // Playback control
        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == page) {

                val prevId = properties.getOrNull(page - 1)
                    ?.videoUrls?.firstOrNull()

                val nextId = properties.getOrNull(page + 1)
                    ?.videoUrls?.firstOrNull()

                viewModel.onPageVisible(
                    mediaId = property.videoUrls.first(),
                    mediaType = MediaType.VOD,
                    previousMediaId = prevId,
                    nextMediaId = nextId
                )
            }
        }

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