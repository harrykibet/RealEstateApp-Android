package com.estatia.realestate.apps.feature.auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextButton
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.auth.R
import com.estatia.realestate.apps.feature.auth.state.PhoneVerificationUiState

@Composable
fun PhoneVerificationDialog(
    phoneNumber: String,
    uiState: PhoneVerificationUiState,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = stringResource(
                        R.string.verification_code_sent,
                        phoneNumber
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(stringResource(R.string.verification_code)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                when (uiState) {

                    is PhoneVerificationUiState.Countdown -> {
                        Text(
                            text = stringResource(
                                R.string.code_expires_in,
                                uiState.secondsLeft / 60,
                                uiState.secondsLeft % 60
                            ),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    PhoneVerificationUiState.Expired -> {
                        Text(
                            text = stringResource(R.string.code_expired),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    is PhoneVerificationUiState.Error -> {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> Unit
                }

                EstatiaButton(
                    onClick = { onVerify(code) },
                    enabled = code.length == 6 &&
                            uiState !is PhoneVerificationUiState.Verifying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState is PhoneVerificationUiState.Verifying) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(stringResource(R.string.verify))
                    }
                }

                if (uiState is PhoneVerificationUiState.Expired) {
                    EstatiaTextButton(
                        onClick = onResend,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.resend_code))
                    }
                }
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

@DevicePreviews
@Composable
fun PhoneVerificationDialogLightPreview(){
    EstatiaTheme {
        EstatiaBackground {
            PhoneVerificationDialog(
                phoneNumber = "1234567890",
                uiState = PhoneVerificationUiState.Countdown(120),
                onVerify = {},
                onResend = {},
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

@DevicePreviews
@Composable
fun PhoneVerificationDialogDarkPreview(){
    EstatiaTheme {
        EstatiaBackground {
            PhoneVerificationDialog(
                phoneNumber = "1234567890",
                uiState = PhoneVerificationUiState.Countdown(120),
                onVerify = {},
                onResend = {},
                onDismiss = {}
            )
        }
    }
}



