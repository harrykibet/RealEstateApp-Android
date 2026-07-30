package com.estatia.realestate.apps.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.estatia.realestate.apps.feature.auth.ui.routes.EmailVerificationRoute
import com.estatia.realestate.apps.feature.auth.ui.routes.ForgotPasswordRoute
import com.estatia.realestate.apps.feature.auth.ui.routes.LoginRoute
import com.estatia.realestate.apps.feature.auth.ui.routes.PhoneVerificationRoute
import com.estatia.realestate.apps.feature.auth.ui.routes.SignUpRoute

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onAuthenticated: () -> Unit
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute
    ) {

        composable<LoginRoute> {
            LoginRoute(
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
            SignUpRoute(
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
            ForgotPasswordRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable<EmailVerificationRoute> {
            EmailVerificationRoute(
                onVerified = onAuthenticated
            )
        }

        composable<PhoneVerificationRoute> {
            PhoneVerificationRoute(
                onDismiss = onAuthenticated
            )
        }
    }
}
