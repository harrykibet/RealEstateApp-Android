package com.estatia.realestate.apps.feature.auth.ui.routes

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.estatia.realestate.apps.feature.auth.state.VerificationUiState
import com.estatia.realestate.apps.feature.auth.ui.screens.VerificationCodeDialog
import com.estatia.realestate.apps.feature.auth.viewModels.VerificationViewModel
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun VerificationDialogRoute(
    verificationId: String,
    phoneNumber: String,
    activity: Activity,
    resendingToken: PhoneAuthProvider.ForceResendingToken,
    onDismiss: () -> Unit,
    viewModel: VerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState is VerificationUiState.Success) {
        onDismiss()
    }

    VerificationCodeDialog(
        phoneNumber = phoneNumber,
        uiState = uiState,
        onVerify = { code ->
            viewModel.verifyCode(verificationId, code)
        },
        onResend = {
            viewModel.resendCode(
                phoneNumber,
                activity,
                resendingToken
            )
        },
        onDismiss = onDismiss
    )
}
