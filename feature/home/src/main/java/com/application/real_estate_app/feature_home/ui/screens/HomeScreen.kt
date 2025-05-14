package com.application.real_estate_app.feature_home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.application.real_estate_app.core_design_system.component.PropertyCard
import com.application.real_estate_app.core_domain.interfaces.IExoplayer
import com.application.real_estate_app.core_model.property.Property

@Composable
fun HomeScreen(
    properties: List<Property>,
    exoPlayer: IExoplayer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(properties) { property ->
            PropertyCard(
                property = property,
                onLikeClick = { /* TODO: handle like */ },
                onCommentClick = { /* TODO: handle comment */ },
                onShareClick = { /* TODO: handle share */ },
                exoPlayer = exoPlayer
            )
        }
    }
}

