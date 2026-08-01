package com.estatia.realestate.apps.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.estatia.realestate.apps.core.navigation.AuthBaseRoute
import com.estatia.realestate.apps.core.navigation.EmailVerificationRoute
import com.estatia.realestate.apps.core.navigation.ForgotPasswordRoute
import com.estatia.realestate.apps.core.navigation.LoginRoute
import com.estatia.realestate.apps.core.navigation.PhoneVerificationRoute
import com.estatia.realestate.apps.core.navigation.SignUpRoute
import com.estatia.realestate.apps.feature.auth.ui.routes.EmailVerificationRoute as EmailVerificationScreen
import com.estatia.realestate.apps.feature.auth.ui.routes.ForgotPasswordRoute as ForgotPasswordScreen
import com.estatia.realestate.apps.feature.auth.ui.routes.LoginRoute as LoginScreen
import com.estatia.realestate.apps.feature.auth.ui.routes.PhoneVerificationRoute as PhoneVerificationScreen
import com.estatia.realestate.apps.feature.auth.ui.routes.SignUpRoute as SignUpScreen

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onAuthenticated: () -> Unit
) {
    navigation<AuthBaseRoute>(
        startDestination = LoginRoute
    ) {

        composable<LoginRoute> {
            LoginScreen(
                onNavigateToHome = onAuthenticated,
                onNavigateToSignUp = {
                    navController.navigate(SignUpRoute)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(ForgotPasswordRoute)
                }
            )
        }

        composable<SignUpRoute> {
            SignUpScreen(
                onEmailVerification = {
                    navController.navigate(EmailVerificationRoute) {
                        popUpTo(SignUpRoute) { inclusive = true }
                    }
                },
                onPhoneVerification = { phoneNumber ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "phoneNumber", phoneNumber
                    )
                    navController.navigate(PhoneVerificationRoute)
                },
                onAlreadyHaveAccount = {
                    navController.popBackStack()
                }
            )
        }

        composable<ForgotPasswordRoute> {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<EmailVerificationRoute> {
            EmailVerificationScreen(
                onVerified = onAuthenticated
            )
        }

        composable<PhoneVerificationRoute> {
            PhoneVerificationScreen(
                onDismiss = onAuthenticated
            )
        }
    }
}
