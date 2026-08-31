package com.estatia.realestate.apps.feature.auth.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaOutlinedButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaText
import com.estatia.realestate.apps.core.designsystem.component.GoogleSignInButton
import com.estatia.realestate.apps.core.designsystem.component.EstatiaBackground
import com.estatia.realestate.apps.core.designsystem.component.EstatiaTextField
import com.estatia.realestate.apps.core.ui.R.drawable.ic_launcher_round
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme
import com.estatia.realestate.apps.core.ui.DevicePreviews
import com.estatia.realestate.apps.feature.auth.R

@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    isLoading: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        EstatiaText(
            text = stringResource(R.string.real_estate_app),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(ic_launcher_round),
            contentDescription = stringResource(R.string.app_icon),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(16.dp))

        EstatiaText(
            text = stringResource(R.string.a_complete_solution_to_property_owners_and_tenants),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(0.9f),
        )

        Spacer(modifier = Modifier.height(40.dp))

        EstatiaTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.email_or_phone_number),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 4.dp)
                .testTag("LoginEmailField"),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EstatiaTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.password),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("LoginPasswordField"),
            isPassword = true,
        )

        Spacer(modifier = Modifier.height(24.dp))

        EstatiaButton(
            onClick = onLoginClick,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
                .testTag("LoginButton"),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
            } else {
                EstatiaText("Login")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GoogleSignInButton(
            isLoading = isLoading,
            enabled = !isLoading,
            onClick = onGoogleSignInClick,
        )

        Spacer(modifier = Modifier.height(24.dp))

        EstatiaOutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier.fillMaxWidth(0.6f),
        ) {
            EstatiaText(
                text = stringResource(R.string.sign_up),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        EstatiaText(
            text = stringResource(R.string.forgot_password),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 8.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    widthDp = 400,
)
@DevicePreviews
@Composable
fun LoginScreenLight() {
    EstatiaTheme {
        EstatiaBackground {
            LoginScreen(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                onLoginClick = {},
                onSignUpClick = {},
                onForgotPasswordClick = {},
                onGoogleSignInClick = {},
            )
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 400,
)
@DevicePreviews
@Composable
fun LoginScreenDark() {
    EstatiaTheme {
        EstatiaBackground {
            LoginScreen(
                email = "",
                onEmailChange = {},
                password = "",
                onPasswordChange = {},
                onLoginClick = {},
                onSignUpClick = {},
                onForgotPasswordClick = {},
                onGoogleSignInClick = {},
            )
        }
    }
}
