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
    navigation(
        route = AuthRoutes.GRAPH,
        startDestination = AuthRoutes.LOGIN
    ) {

        composable(AuthRoutes.LOGIN) {
            LoginRoute(
                onNavigateToHome = onAuthenticated,
                onNavigateToSignUp = {
                    navController.navigate(AuthRoutes.SIGN_UP)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(AuthRoutes.FORGOT_PASSWORD)
                }
            )
        }

        composable(AuthRoutes.SIGN_UP) {
            SignUpRoute(
                onSignUpSuccess = {
                    navController.navigate(AuthRoutes.EMAIL_VERIFICATION) {
                        popUpTo(AuthRoutes.SIGN_UP) { inclusive = true }
                    }
                },
                onAlreadyHaveAccount = {
                    navController.popBackStack()
                }
            )
        }

        composable(AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordRoute(
                onBack = { navController.popBackStack() }
            )
        }

        composable(AuthRoutes.EMAIL_VERIFICATION) {
            EmailVerificationRoute(
                onVerified = onAuthenticated
            )
        }

        composable(route = AuthRoutes.PHONE_VERIFICATION) {
            PhoneVerificationRoute(
                onDismiss = onAuthenticated
            )
        }
    }
}
