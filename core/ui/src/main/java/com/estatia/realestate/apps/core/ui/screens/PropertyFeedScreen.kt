package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.estatia.realestate.apps.core.model.property.ListingUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyFeedScreen(
    listings: List<ListingUiModel>,
    playbackCoordinator: @Composable (PagerState, List<ListingUiModel>) -> Unit,
    itemContent: @Composable (ListingUiModel, Boolean, (String) -> Unit) -> Unit,
    commentsContent: @Composable (String) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { listings.size }
    )

    var showCommentsForId by remember { mutableStateOf<String?>(null) }

    playbackCoordinator(pagerState, listings)

    BoxWithBottomSheet(
        showSheet = showCommentsForId != null,
        onDismissSheet = { showCommentsForId = null },
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

            itemContent(
                listing,
                pagerState.currentPage == page,
                { id -> showCommentsForId = id }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxWithBottomSheet(
    showSheet: Boolean,
    onDismissSheet: () -> Unit,
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
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
