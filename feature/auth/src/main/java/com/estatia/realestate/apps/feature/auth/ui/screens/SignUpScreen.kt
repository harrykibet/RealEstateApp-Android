package com.estatia.realestate.apps.feature.auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.auth.R
import com.estatia.realestate.apps.feature.auth.actions.SignUpAction
import com.estatia.realestate.apps.feature.auth.state.SignUpFormState

@Composable
fun SignUpScreen(
    state: SignUpFormState,
    onAction: (SignUpAction) -> Unit,
    onAlreadyHaveAccountClick: () -> Unit,
) {
    var expandedDropdown by remember { mutableStateOf(value = false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {

        EstatiaText(
            text = stringResource(id = R.string.create_new_account),
            style = MaterialTheme.typography.headlineMedium,
        )

        EstatiaTextField(
            value = state.userName,
            onValueChange = {
                onAction(SignUpAction.UserNameChanged(it))
            },
            label = stringResource(id = R.string.username),
            modifier = Modifier.fillMaxWidth(),
        )

        EstatiaTextField(
            value = state.email,
            onValueChange = {
                onAction(SignUpAction.EmailChanged(it))
            },
            label = stringResource(id = R.string.email_address),
            modifier = Modifier.fillMaxWidth(),
        )

        EstatiaTextField(
            value = state.phone,
            onValueChange = {
                onAction(SignUpAction.PhoneChanged(it))
            },
            label = stringResource(id = R.string.phone_number),
            modifier = Modifier.fillMaxWidth(),
        )

        EstatiaTextField(
            value = state.password,
            onValueChange = {
                onAction(SignUpAction.PasswordChanged(it))
            },
            label = stringResource(id = R.string.password2),
            isPassword = true,
            modifier = Modifier.fillMaxWidth(),
        )

        EstatiaText(
            text = state.userType.ifEmpty {
                stringResource(id = R.string.select_user_type)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedDropdown = true }
                .padding(16.dp),
        )

        UserTypeDropdownMenu(
            expanded = expandedDropdown,
            onDismissRequest = { expandedDropdown = false },
        ) {
            onAction(SignUpAction.UserTypeChanged(it))
            expandedDropdown = false
        }

        state.error?.let { error ->
            EstatiaText(
                text = error,
                color = MaterialTheme.colorScheme.error,
            )
        }

        EstatiaButton(
            onClick = { onAction(SignUpAction.Submit) },
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(52.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                EstatiaText(stringResource(id = R.string.sign_up))
            }
        }

        EstatiaTextButton(onClick = onAlreadyHaveAccountClick) {
            EstatiaText(stringResource(id = R.string.already_have_an_account_log_in))
        }
    }
}


@Composable
fun UserTypeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectUserType: (String) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        // Tenant Option
        DropdownMenuItem(
            onClick = {
                onSelectUserType("Tenant")
                onDismissRequest()
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "Tenant Icon")
            },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
            },
            enabled = true,
            colors = MenuItemColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                leadingIconColor = MaterialTheme.colorScheme.onSurface,
                trailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            interactionSource = remember { MutableInteractionSource() },
            text = { EstatiaText(text = "Tenant") }
        )

        // Landlord Option
        DropdownMenuItem(
            onClick = {
                onSelectUserType("Landlord")
                onDismissRequest()
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Landlord Icon")
            },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
            },
            enabled = true,
            colors = MenuItemColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                leadingIconColor = MaterialTheme.colorScheme.onSurface,
                trailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            interactionSource = remember { MutableInteractionSource() },
            text = { EstatiaText(text = "Landlord") }
        )

        // Agent Option
        DropdownMenuItem(
            onClick = {
                onSelectUserType("Agent")
                onDismissRequest()
            },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Work, contentDescription = "Agent Icon")
            },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Selected")
            },
            enabled = true,
            colors = MenuItemColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                leadingIconColor = MaterialTheme.colorScheme.onSurface,
                trailingIconColor = MaterialTheme.colorScheme.primaryContainer,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
            interactionSource = remember { MutableInteractionSource() },
            text = { EstatiaText(text = "Agent") }
        )
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
fun SignUpScreenLightPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SignUpScreen(
                state = SignUpFormState(),
                onAction = {},
            ) { }
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
fun SignUpScreenDarkPreview() {
    EstatiaTheme {
        EstatiaBackground {
            SignUpScreen(
                state = SignUpFormState(),
                onAction = {},
            ) { }
        }
    }
}

