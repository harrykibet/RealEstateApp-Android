package com.estatia.realestate.apps.feature.auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextButton
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.auth.state.EmailVerificationUiState

@Composable
fun EmailVerificationDialog(
    uiState: EmailVerificationUiState,
    onSendEmail: () -> Unit,
    onCheckVerification: () -> Unit,
    onVerified: () -> Unit
) {
    LaunchedEffect(uiState) {
        if (uiState is EmailVerificationUiState.Verified) {
            onVerified()
        }
    }

    AlertDialog(
        onDismissRequest = {}, // 🔐 auth-critical → not dismissible
        confirmButton = {},
        title = {
            EstatiaText(
                text = "Verify your email",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            EmailVerificationContent(
                uiState = uiState,
                onSendEmail = onSendEmail,
                onCheckVerification = onCheckVerification
            )
        }
    )
}

@Composable
private fun EmailVerificationContent(
    uiState: EmailVerificationUiState,
    onSendEmail: () -> Unit,
    onCheckVerification: () -> Unit
) {
    when (uiState) {

        EmailVerificationUiState.Idle -> {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EstatiaText(
                    text = "We need to verify your email address to continue.",
                    style = MaterialTheme.typography.bodyMedium
                )
                EstatiaButton(
                    onClick = onSendEmail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EstatiaText("Send verification email")
                }
            }
        }

        EmailVerificationUiState.Sending,
        EmailVerificationUiState.Checking -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                EstatiaText("Please wait…")
            }
        }

        EmailVerificationUiState.EmailSent -> {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EstatiaText(
                    text = "We’ve sent a verification email. Please check your inbox.",
                    style = MaterialTheme.typography.bodyMedium
                )

                EstatiaButton(
                    onClick = onCheckVerification,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EstatiaText("I’ve verified my email")
                }

                EstatiaTextButton(
                    onClick = onSendEmail,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    EstatiaText("Resend email")
                }
            }
        }

        is EmailVerificationUiState.Error -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EstatiaText(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error
                )

                EstatiaButton(
                    onClick = onSendEmail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EstatiaText("Try again")
                }
            }
        }

        EmailVerificationUiState.Verified -> {
            // handled via LaunchedEffect → navigation
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)

@DevicePreviews
@Composable
fun EmailVerificationDialogLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            EmailVerificationDialog(
                uiState = EmailVerificationUiState.Idle,
                onSendEmail = {},
                onCheckVerification = {},
                onVerified = {}
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400
)

@DevicePreviews
@Composable
fun EmailVerificationDialogDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            EmailVerificationDialog(
                uiState = EmailVerificationUiState.Idle,
                onSendEmail = {},
                onCheckVerification = {},
                onVerified = {}
            )
        }
    }
}


