package com.application.real_estate_app.ui.activities

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.application.real_estate_app.feature_profile.ui.screens.ProfileScreen
import com.application.real_estate_app.feature_auth.ui.screens.LoginScreen
import com.application.real_estate_app.feature_home.ui.screens.HomeScreen
import com.application.real_estate_app.feature_property.ui.screens.PropertyFormScreen
import com.application.real_estate_app.feature_favorites.ui.screens.FavoritesScreen
import com.application.real_estate_app.feature_search.ui.screens.MapWithSearchBar

@Composable
fun AppNavHost(navController: NavHostController, isAuthenticated: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "login"
    ) {
        composable("home") { HomeScreen() }
        composable("explore") { MapWithSearchBar() }
        composable("add") { PropertyFormScreen() }
        composable("favorites") { FavoritesScreen() }
        composable("profile") { ProfileScreen() }
        composable("login") { LoginScreen() }
    }
}
