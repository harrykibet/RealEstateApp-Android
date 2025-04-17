package com.application.real_estate_app.ui.activities

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(navController: NavHostController, isAuthenticated: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
    ) {
        composable("home") { HomeScreen() }
        composable("explore") { ExploreScreen() }
        composable("add") { AddPropertyScreen() }
        composable("favorites") { FavoritesScreen() }
        composable("profile") { ProfileScreen() }
        composable("login") { LoginScreen() }
    }
}
