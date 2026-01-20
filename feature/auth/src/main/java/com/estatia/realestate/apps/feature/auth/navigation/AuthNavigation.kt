package com.estatia.realestate.apps.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.auth.ui.routes.LoginRoute
import kotlinx.serialization.Serializable

@Serializable
data object AuthBaseRoute // route to base navigation graph

@Serializable
data object LoginRoute // route to login screen

@Serializable
data object VerificationRoute // route to verification screen

@Serializable
data object SignUpRoute // route to sign up screen

@Serializable
data object ForgotPasswordRoute // route to forgot password screen

@Serializable
data class VerifyEmailRoute(val email: String) // route to verify email screen

fun NavController.navigateToLogin(navOptions: NavOptions? = null) = navigate(route = LoginRoute, navOptions)

fun NavGraphBuilder.authGraph(
    onAuthenticated: () -> Unit
) {
    navigation<AuthBaseRoute>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginRoute(
                onNavigateToHome = onAuthenticated,
                onNavigateToSignUp = { /*navigateToSignUp()*/ },
                onNavigateToForgotPassword = { /*navigateToForgotPassword()*/ }
            )
        }
        /*composable<SignUpRoute> {
            SignUpRoute (
                onNavigateToLogin = { navigateToLogin() }
            )
        }
        composable<ForgotPasswordRoute> {
            ForgotPasswordRoute(
                onNavigateToLogin = { navigateToLogin() }
            )
        }
        composable<VerifyEmailRoute> { backStackEntry ->
            VerifyEmailRoute(
                email = backStackEntry.arguments?.getString("email") ?: "",
                onNavigateToLogin = { navigateToLogin() }
            )
        }*/
    }
}