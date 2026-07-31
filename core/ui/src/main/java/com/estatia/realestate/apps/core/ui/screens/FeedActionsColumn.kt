package com.estatia.realestate.apps.core.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.FeedActionButton
import com.estatia.realestate.apps.core.model.property.ListingUiModel

@Composable
fun FeedActionsColumn(
    listing: ListingUiModel,
    onLikeClick: (ListingUiModel) -> Unit,
    onCommentClick: (ListingUiModel) -> Unit,
    onShareClick: (ListingUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        FeedActionButton(
            icon = Icons.Default.Favorite,
            count = listing.likesCount,
            onClick = { onLikeClick(listing) }
        )

        FeedActionButton(
            icon = Icons.AutoMirrored.Default.Comment,
            count = listing.commentsCount,
            onClick = { onCommentClick(listing) }
        )

        FeedActionButton(
            icon = Icons.Default.Share,
            count = listing.sharesCount,
            onClick = { onShareClick(listing) }
        )
    }
}
