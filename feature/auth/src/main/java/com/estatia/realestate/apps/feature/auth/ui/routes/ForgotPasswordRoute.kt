package com.estatia.realestate.apps.feature.auth.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.feature.auth.state.ForgotPasswordAction
import com.estatia.realestate.apps.feature.auth.ui.screens.ForgotPasswordDialog
import com.estatia.realestate.apps.feature.auth.viewModels.ForgotPasswordViewModel

@Composable
fun ForgotPasswordRoute(
    onBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordDialog(
        state = uiState,
        onEmailChange = {
            viewModel.onAction(ForgotPasswordAction.EmailChanged(it))
        },
        onSubmit = {
            viewModel.onAction(ForgotPasswordAction.Submit)
        },
        onRetry = {
            viewModel.onAction(ForgotPasswordAction.Retry)
        },
        onDismiss = onBack
    )
}
