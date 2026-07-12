package com.estatia.realestate.apps.feature.home.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel

@Composable
internal fun HomeRoute(
    onNavigateToPropertyDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val state by viewModel.uiState.collectAsState()

    PropertyFeedScreen(
        properties = state.properties,

        onLikeClick = { property ->
            // viewModel.toggleLike(property)
        },

        onCommentClick = { property ->
            // viewModel.openComments(property)
        },

        onShareClick = { property ->
            // viewModel.shareProperty(property)
        }
    )
}