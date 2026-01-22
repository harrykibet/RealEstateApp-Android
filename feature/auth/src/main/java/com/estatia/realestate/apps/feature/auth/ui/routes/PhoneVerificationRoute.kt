package com.estatia.realestate.apps.feature.auth.ui.routes

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.estatia.realestate.apps.feature.auth.state.PhoneVerificationUiState
import com.estatia.realestate.apps.feature.auth.ui.screens.PhoneVerificationDialog
import com.estatia.realestate.apps.feature.auth.viewModels.PhoneVerificationViewModel

@Composable
fun PhoneVerificationRoute(
    verificationId: String,
    phoneNumber: String,
    onDismiss: () -> Unit,
    viewModel: PhoneVerificationViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.attachActivity(activity)
    }

    if (uiState is PhoneVerificationUiState.Success) {
        onDismiss()
    }

    PhoneVerificationDialog(
        phoneNumber = phoneNumber,
        uiState = uiState,
        onVerify = { code ->
            viewModel.verifyCode(verificationId, code)
        },
        onResend = {
            viewModel.resendCode(phoneNumber)
        },
        onDismiss = onDismiss
    )
}

