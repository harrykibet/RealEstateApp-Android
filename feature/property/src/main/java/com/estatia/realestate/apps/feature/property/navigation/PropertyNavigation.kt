package com.estatia.realestate.apps.feature.property.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyDetailRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyMediaCaptureRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyRoute
import com.estatia.realestate.apps.feature.property.ui.screens.PropertyDetailsRoute
import com.estatia.realestate.apps.feature.property.ui.screens.PropertyFormScreen
import com.estatia.realestate.apps.feature.property.ui.screens.PropertyMediaCaptureScreen

fun NavController.navigateToPropertyForm(navOptions: NavOptions? = null) =
    navigate(route = PropertyRoute, navOptions)

fun NavGraphBuilder.propertyDetailsScreen(
    onBackClick: () -> Unit,
) {
    composable<PropertyDetailRoute> { backStackEntry ->
        val route: PropertyDetailRoute = backStackEntry.toRoute()
        PropertyDetailsRoute(
            propertyId = route.propertyId,
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.propertyAdditionGraph(
    navController: NavController,
    onBackClick: () -> Unit,
) {
    navigation<PropertyBaseRoute>(startDestination = PropertyMediaCaptureRoute) {
        composable<PropertyMediaCaptureRoute> {
            PropertyMediaCaptureScreen(
                onContinue = { navController.navigate(PropertyRoute) }
            )
        }
        composable<PropertyRoute> {
            PropertyFormScreen()
        }
    }
}
