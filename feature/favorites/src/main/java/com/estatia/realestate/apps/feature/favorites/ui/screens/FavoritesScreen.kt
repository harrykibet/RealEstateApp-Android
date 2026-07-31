package com.estatia.realestate.apps.feature.favorites.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.model.property.toListingUiModel
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen


@Composable
fun FavoritesScreen(
    favoriteProperties: List<PropertyDomainModel>,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onPropertyClick: (String) -> Unit
) {
    val listings = remember(favoriteProperties) {
        favoriteProperties.map { it.toListingUiModel() }
    }

    PropertyFeedScreen(
        listings = listings,
        onLikeClick = { listing -> onLikeClick(listing.id) },
        onCommentClick = { listing -> onCommentClick(listing.id) },
        onShareClick = { listing -> onShareClick(listing.id) },
        onPropertyClick = { listing -> onPropertyClick(listing.id) }
    )
}
