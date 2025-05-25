package com.application.real_estate_app.feature_auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.application.real_estate_app.core_design_system.component.ReaBackground
import com.application.real_estate_app.core_design_system.component.RoundedElevatedTextField
import com.application.real_estate_app.core_design_system.theme.ReaTheme
import com.application.real_estate_app.core_ui.DevicePreviews
import com.application.real_estate_app.feature_auth.R

@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit, onAlreadyHaveAccountClick: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Name
        Text(
            text = stringResource(id = R.string.create_new_account),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Username Input
        RoundedElevatedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = stringResource(id = R.string.username),
            modifier = Modifier.fillMaxWidth()
        )

        // Email Input
        RoundedElevatedTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(id = R.string.email_address),
            modifier = Modifier.fillMaxWidth()
        )

        // Phone Input
        RoundedElevatedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = stringResource(id = R.string.phone_number),
            modifier = Modifier.fillMaxWidth()
        )

        // Password Input
        RoundedElevatedTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(id = R.string.password2),
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        // User Type Dropdown (Spinner Replacement)
        Text(
            text = userType.ifEmpty { stringResource(id = R.string.select_user_type) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedDropdown = true }
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium)

        UserTypeDropdownMenu(
            expanded = expandedDropdown,
            onDismissRequest = { expandedDropdown = false },
            onSelectUserType = { selectedType ->
                userType = selectedType
                expandedDropdown = false
            })

        // Sign-Up Button
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(52.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.sign_up),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Already have an account text
        TextButton(
            onClick = onAlreadyHaveAccountClick, modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.already_have_an_account_log_in),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UserTypeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectUserType: (String) -> Unit
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
            text = { Text(text = "Tenant") }
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
            text = { Text(text = "Landlord") }
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
            text = { Text(text = "Agent") }
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
    ReaTheme {
        ReaBackground {
            SignUpScreen(
                onSignUpClick = {},
                onAlreadyHaveAccountClick = {}
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
fun SignUpScreenDarkPreview() {
    ReaTheme {
        ReaBackground {
            SignUpScreen(
                onSignUpClick = {},
                onAlreadyHaveAccountClick = {}
            )
        }
    }
}

