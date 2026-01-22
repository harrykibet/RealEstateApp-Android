package com.estatia.realestate.apps.feature.auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.auth.state.ForgotPasswordUiState

@Composable
fun ForgotPasswordDialog(
    state: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset password",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            ForgotPasswordContent(
                state = state,
                onEmailChange = onEmailChange
            )
        },
        confirmButton = {
            ForgotPasswordConfirmButton(
                state = state,
                onSubmit = onSubmit,
                onRetry = onRetry,
                onDismiss = onDismiss
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit
) {
    when (state) {
        is ForgotPasswordUiState.Idle -> {
            EmailInput(
                email = state.email,
                enabled = true,
                onEmailChange = onEmailChange
            )
        }

        is ForgotPasswordUiState.Loading -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EmailInput(
                    email = state.email,
                    enabled = false,
                    onEmailChange = {}
                )
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        is ForgotPasswordUiState.Success -> {
            Text(
                text = "A password reset link has been sent to\n${state.email}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        is ForgotPasswordUiState.Error -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EmailInput(
                    email = state.email,
                    enabled = true,
                    onEmailChange = onEmailChange
                )
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun EmailInput(
    email: String,
    enabled: Boolean,
    onEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email address") },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true
    )
}

@Composable
private fun ForgotPasswordConfirmButton(
    state: ForgotPasswordUiState,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    when (state) {
        is ForgotPasswordUiState.Idle,
        is ForgotPasswordUiState.Error -> {
            Button(onClick = onSubmit) {
                Text("Send reset link")
            }
        }

        is ForgotPasswordUiState.Loading -> {
            Button(
                onClick = {},
                enabled = false
            ) {
                Text("Sending…")
            }
        }

        is ForgotPasswordUiState.Success -> {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400
)

@Composable
@DevicePreviews
fun ForgotPasswordDialogLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ForgotPasswordDialog(
                state = ForgotPasswordUiState.Idle(email = ""),
                onEmailChange = {},
                onSubmit = {},
                onRetry = {},
                onDismiss = {}
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

@Composable
@DevicePreviews
fun ForgotPasswordDialogDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            ForgotPasswordDialog(
                state = ForgotPasswordUiState.Idle(email = ""),
                onEmailChange = {},
                onSubmit = {},
                onRetry = {},
                onDismiss = {}
            )
        }
    }
}

