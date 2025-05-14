package com.application.real_estate_app.feature_home.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.application.real_estate_app.feature_home.ui.screens.HomeScreen
import com.application.real_estate_app.feature_home.ui.viewModels.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute // route to Home screen

@Serializable
data object HomeBaseRoute // route to base navigation graph

fun NavGraphBuilder.homeRoute() {
    composable<HomeRoute> {
        val viewModel: HomeViewModel = hiltViewModel()
        HomeScreen(
            properties = viewModel.properties,
            exoPlayer = viewModel.exoPlayer
        )
    }
}