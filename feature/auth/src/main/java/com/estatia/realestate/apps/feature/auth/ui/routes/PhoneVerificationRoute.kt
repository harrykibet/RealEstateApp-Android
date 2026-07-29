package com.estatia.realestate.apps.feature.auth.ui.routes

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.feature.auth.state.PhoneVerificationUiState
import com.estatia.realestate.apps.feature.auth.ui.screens.PhoneVerificationDialog
import com.estatia.realestate.apps.feature.auth.viewModels.PhoneVerificationViewModel

@Composable
fun PhoneVerificationRoute(
    onDismiss: () -> Unit,
    viewModel: PhoneVerificationViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val activity = LocalContext.current as Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.startPhoneNumberVerification(activity)
    }

    if (uiState is PhoneVerificationUiState.Success) {
        onDismiss()
    }

    PhoneVerificationDialog(
        phoneNumber = viewModel.phoneNumber,
        uiState = uiState,
        onVerify = { code ->
            val state = uiState
            if (state is PhoneVerificationUiState.CodeSent) {
                viewModel.verifyCode(state.verificationId, code)
            }
        },
        onResend = {
            viewModel.resendCode(activity)
        },
        onDismiss = onDismiss
    )
}


