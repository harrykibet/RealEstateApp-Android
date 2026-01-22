package com.estatia.realestate.apps.feature.auth.ui.routes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.estatia.realestate.apps.feature.auth.ui.screens.EmailVerificationDialog
import com.estatia.realestate.apps.feature.auth.viewModels.EmailVerificationViewModel

@Composable
fun EmailVerificationRoute(
    onVerified: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EmailVerificationDialog(
        uiState = uiState,
        onSendEmail = viewModel::sendVerificationEmail,
        onCheckVerification = viewModel::checkVerificationStatus,
        onVerified = onVerified
    )
}
