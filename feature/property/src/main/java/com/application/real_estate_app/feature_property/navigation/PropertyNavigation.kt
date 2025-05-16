package com.application.real_estate_app.feature_property.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.application.real_estate_app.feature_property.ui.screens.PropertyFormScreen
import kotlinx.serialization.Serializable

@Serializable data object PropertyRoute // route to Property screen

fun NavGraphBuilder.propertyRoute() {
    composable<PropertyRoute> {
        PropertyFormScreen()
    }
}