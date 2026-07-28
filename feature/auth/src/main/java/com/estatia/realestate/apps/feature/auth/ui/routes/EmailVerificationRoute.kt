package com.estatia.realestate.apps.feature.auth.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.feature.auth.ui.screens.EmailVerificationDialog
import com.estatia.realestate.apps.feature.auth.viewModels.EmailVerificationViewModel

@Composable
fun EmailVerificationRoute(
    onVerified: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EmailVerificationDialog(
        uiState = uiState,
        onSendEmail = viewModel::sendVerificationEmail,
        onCheckVerification = viewModel::checkVerificationStatus,
        onVerified = onVerified
    )
}
