package com.estatia.realestate.apps.feature.favorites.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.estatia.realestate.apps.core.ui.PropertyCard
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer
import com.estatia.realestate.apps.core.model.property.Property

@Composable
fun FavoritesScreen() {
    val likedProperties: List<Property> = emptyList() // Replace with actual data
    val exoPlayer: IExoplayer = hiltViewModel()  // Get ExoPlayer instance
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(likedProperties) { property ->
            PropertyCard(
                property = property,
                onLikeClick = { /* handle like */ },
                onCommentClick = { /* handle comment */ },
                onShareClick = { /* handle share */ },
                exoPlayer = exoPlayer,
                onPropertyClick = { /* handle property click */ }
            )
        }
    }
}
