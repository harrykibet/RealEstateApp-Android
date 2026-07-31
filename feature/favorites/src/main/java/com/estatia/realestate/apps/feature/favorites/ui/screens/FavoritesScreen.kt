package com.estatia.realestate.apps.feature.favorites.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.ui.screens.CommentSheetContent
import com.estatia.realestate.apps.feature.comments.ui.viewmodels.CommentsViewModel


@Composable
fun FavoritesScreen(
    favoriteProperties: List<PropertyDomainModel>,
    onLikeClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onPropertyClick: (String) -> Unit
) {
    val listings = remember(favoriteProperties) {
        favoriteProperties.map { it.toListingUiModel() }
    }

    PropertyFeedScreen(
        listings = listings,
        onLikeClick = { listing -> onLikeClick(listing.id) },
        onShareClick = { listing -> onShareClick(listing.id) },
        onPropertyClick = { listing -> onPropertyClick(listing.id) },
        commentsContent = { propertyId ->
            val commentsViewModel: CommentsViewModel = hiltViewModel()
            LaunchedEffect(propertyId) {
                commentsViewModel.onAction(CommentsAction.Load(propertyId))
            }
            val commentsState by commentsViewModel.state.collectAsState()
            CommentSheetContent(
                state = commentsState,
                onAction = commentsViewModel::onAction
            )
        }
    )
}
