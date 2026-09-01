package com.estatia.realestate.apps.feature.auth.ui.routes

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.estatia.realestate.apps.feature.auth.R
import com.estatia.realestate.apps.feature.auth.state.AuthState
import com.estatia.realestate.apps.feature.auth.ui.screens.LoginScreen
import com.estatia.realestate.apps.feature.auth.viewModels.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch


@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    ),
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    /* -----------------------------------------
     * React to AuthState (single source of truth)
     * ----------------------------------------- */
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onNavigateToHome()
            }

            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    (authState as AuthState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> Unit
        }
    }

    LoginScreen(
        email = email,
        onEmailChange = { email = it },

        password = password,
        onPasswordChange = { password = it },

        onLoginClick = {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@LoginScreen
            }
            viewModel.loginWithEmail(email, password)
        },

        onGoogleSignInClick = {
            scope.launch {
                signInWithGoogleCredentialManager(
                    context = context,
                    onSuccess = { idToken ->
                        viewModel.loginWithGoogleIdToken(idToken)
                    },
                    onError = { throwable ->
                        Toast.makeText(
                            context,
                            throwable.message ?: "Google sign-in failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        },

        onSignUpClick = onNavigateToSignUp,
        onForgotPasswordClick = onNavigateToForgotPassword,

        isLoading = authState is AuthState.Loading
    )
}

suspend fun signInWithGoogleCredentialManager(
    context: Context,
    onSuccess: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(
                context.getString(R.string.default_web_client_id)
            )
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            onSuccess(googleIdTokenCredential.idToken)
        } else {
            onError(IllegalStateException("Unexpected credential type"))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        onError(e)
    }
}
