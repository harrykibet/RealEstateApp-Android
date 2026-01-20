package com.estatia.realestate.apps.feature.auth.ui.routes

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.estatia.realestate.apps.feature.auth.ui.screens.LoginScreen
import com.estatia.realestate.apps.feature.auth.viewModels.AuthViewModel
import android.widget.Toast
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun LoginRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val googleSignInResult by viewModel.googleSignInResult.observeAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.observeAsState()

    // Handle Google Sign-in result
    LaunchedEffect(googleSignInResult) {
        googleSignInResult?.let {
            it.onSuccess {
                onNavigateToHome()
            }.onFailure { error ->
                Toast.makeText(context, error.message ?: "Google sign-in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Check if user is already logged in
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn == true) {
            onNavigateToHome()
        }
    }

    LoginScreen(
        onLoginClick = {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@LoginScreen
            }

            viewModel.loginUser(email, password) { e ->
                Toast.makeText(context, e.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }?.addOnSuccessListener {
                onNavigateToHome()
            }?.addOnFailureListener {
                Toast.makeText(context, it.message ?: "Login failed", Toast.LENGTH_SHORT).show()
            }
        },
        onSignUpClick = onNavigateToSignUp,
        onForgotPasswordClick = onNavigateToForgotPassword,
        onGoogleSignInClick = {
            viewModel.signInWithGoogle(context as? Activity ?: return@LoginScreen)
        },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it }
    )
}
