package com.application.real_estate_app.feature_property.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable data object PropertyBaseRoute // route to base navigation graph

@Serializable data object PropertyRoute // route to Property screen

fun NavController.navigateToPropertyForm(navOptions: NavOptions? = null) = navigate(route = PropertyRoute, navOptions)

fun NavGraphBuilder.propertyAdditionGraph(
    onBackClick: () -> Unit
) {
    navigation<PropertyBaseRoute>(startDestination = PropertyRoute) {
        composable<PropertyRoute> {
            PropertyRoute()
        }
    }
}