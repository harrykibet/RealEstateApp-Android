package com.estatia.realestate.apps.feature.shared_ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.core.model.property.ListingUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFeedScreen(
    listings: List<ListingUiModel>,
    playbackCoordinator: @Composable (PagerState, List<ListingUiModel>) -> Unit,
    itemContent: @Composable (ListingUiModel, Boolean, (String) -> Unit) -> Unit,
    commentsContent: @Composable (String) -> Unit,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    onMeteredNetworkDetected: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {},
    autoAdvanceEvent: kotlinx.coroutines.flow.Flow<Unit>? = null,
    onAutoAdvanceAction: () -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { listings.size }
    )

    var showCommentsForId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    LaunchedEffect(autoAdvanceEvent) {
        autoAdvanceEvent?.collect {
            if (pagerState.currentPage < listings.size - 1) {
                onAutoAdvanceAction()
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    playbackCoordinator(pagerState, listings)

    BoxWithBottomSheet(
        showSheet = showCommentsForId != null,
        onDismissSheet = { showCommentsForId = null },
        modifier = modifier,
        sheetContent = {
            showCommentsForId?.let { id ->
                commentsContent(id)
            }
        }
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            val listing = listings[page]

            // TikTok-style horizontal swipe for details (Start at page 1, swipe right to page 0)
            val horizontalPagerState = rememberPagerState(
                initialPage = 1,
                pageCount = { 2 }
            )

            LaunchedEffect(horizontalPagerState) {
                snapshotFlow { horizontalPagerState.currentPage }
                    .collect { horizontalPage ->
                        if (horizontalPage == 0) {
                            onNavigateToDetails(listing.id)
                            // Snap back to 1 so when we come back, we're on the video
                            horizontalPagerState.scrollToPage(1)
                        }
                    }
            }

            HorizontalPager(
                state = horizontalPagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { hPage ->
                if (hPage == 1) {
                    itemContent(
                        listing,
                        pagerState.currentPage == page,
                        { id -> showCommentsForId = id }
                    )
                } else {
                    // Empty page (left side) to trigger navigation
                    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithBottomSheet(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    sheetContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        content()

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = onDismissSheet,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                modifier = Modifier.fillMaxWidth(),
                dragHandle = {
                    androidx.compose.material3.BottomSheetDefaults.DragHandle()
                }
            ) {
                sheetContent()
            }
        }
    }
}
