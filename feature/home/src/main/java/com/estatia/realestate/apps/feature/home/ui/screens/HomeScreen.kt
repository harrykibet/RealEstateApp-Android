package com.estatia.realestate.apps.feature.home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.estatia.realestate.apps.core.ui.PropertyCard
import com.estatia.realestate.apps.core.model.property.Property
import com.estatia.realestate.apps.feature.home.ui.HomeUiState
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel

@Composable
internal fun HomeRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()

    HomeScreen(
        state = state,
        onPropertyClick = { property ->
            property.id?.let { onNavigateToPropertyDetail(it) }
        },
        onBackClick = onBackClick,
        onLikeClick = { property ->
            //viewModel.toggleLike(property)
        },
        onCommentClick = { property ->
            //viewModel.toggleComment(property)
        },
        onShareClick = { property ->
            //viewModel.shareProperty(property)
        },
        exoPlayer = viewModel.exoPlayer
    )
}


@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onBackClick: () -> Unit,
    onPropertyClick: (Property) -> Unit,
    onLikeClick: (Property) -> Unit,
    onCommentClick: (Property) -> Unit,
    onShareClick: (Property) -> Unit,
    exoPlayer: IPlayer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        items(state.properties) { property ->
            PropertyCard(
                property = property,
                onPropertyClick = onPropertyClick,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick,
                onShareClick = onShareClick,
                exoPlayer = exoPlayer
            )
        }
    }
}

