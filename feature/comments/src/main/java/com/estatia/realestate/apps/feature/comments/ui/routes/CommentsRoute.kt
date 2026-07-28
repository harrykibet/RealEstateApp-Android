package com.estatia.realestate.apps.feature.comments.ui.routes

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.feature.comments.actions.CommentsAction
import com.estatia.realestate.apps.feature.comments.events.CommentsEvent
import com.estatia.realestate.apps.feature.comments.ui.screens.CommentsScreen
import com.estatia.realestate.apps.feature.comments.ui.viewmodels.CommentsViewModel

@Composable
fun CommentsRoute(
    propertyId: String,
    onBack: () -> Unit,
    viewModel: CommentsViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize once per propertyId
    LaunchedEffect(propertyId) {
        viewModel.onAction(
            CommentsAction.Load(propertyId)
        )
    }

    // One-off UI events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CommentsEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    CommentsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        onBack = onBack
    )
}
