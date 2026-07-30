package com.estatia.realestate.apps.feature.home.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.core.ui.screens.PropertyFeedScreen
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel

@Composable
internal fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(
        viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
    ),
) {
    val state by viewModel.uiState.collectAsState()

    PropertyFeedScreen(
        properties = state.properties,

        onLikeClick = {
            // viewModel.toggleLike(it)
        },

        onCommentClick = {
            // viewModel.openComments(it)
        },
    ) {
        // viewModel.shareProperty(it)
    }
}