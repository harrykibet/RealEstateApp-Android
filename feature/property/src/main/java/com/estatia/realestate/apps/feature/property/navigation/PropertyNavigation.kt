package com.estatia.realestate.apps.feature.property.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.estatia.realestate.apps.feature.property.ui.screens.PropertyFormScreen

fun NavController.navigateToPropertyForm(navOptions: NavOptions? = null) =
    navigate(route = PropertyRoute, navOptions)

fun NavGraphBuilder.propertyAdditionGraph(
    onBackClick: () -> Unit,
) {
    navigation<PropertyBaseRoute>(startDestination = PropertyRoute) {
        composable<PropertyRoute> {
            PropertyFormScreen()
        }
    }
}
