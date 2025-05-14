package com.application.real_estate_app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.application.real_estate_app.navigation.AppNavHost

// RealEstateApp.kt (inside app module)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean,
    navController: NavHostController = rememberNavController()

) {
    Scaffold(
        bottomBar = {
            if (isAuthenticated) {
                BottomNavBar(navController = navController)
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavHost(navController = navController, isAuthenticated = isAuthenticated)
        }
    }
}
