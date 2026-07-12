package com.estatia.realestate.apps.feature.favorites.ui.screens

import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen


@Composable
fun FavoritesScreen(
    favoriteProperties: List<PropertyDomainModel>,
    onLikeClick: (PropertyDomainModel) -> Unit,
    onCommentClick: (PropertyDomainModel) -> Unit,
    onShareClick: (PropertyDomainModel) -> Unit,
    onPropertyClick: (PropertyDomainModel) -> Unit
) {


    PropertyFeedScreen(
        properties = favoriteProperties,

        onLikeClick = onLikeClick,

        onCommentClick = onCommentClick,

        onShareClick = onShareClick
    )
}