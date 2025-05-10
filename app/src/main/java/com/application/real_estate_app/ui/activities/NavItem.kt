package com.application.real_estate_app.ui.activities

import androidx.compose.ui.graphics.vector.ImageVector
import com.application.real_estate_app.core_design_system.icons.RealEstateIcons

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : NavItem("home", "Home", RealEstateIcons.Home)
    data object Explore : NavItem("explore", "Explore", RealEstateIcons.Explore)
    data object Add : NavItem("add", "Add", RealEstateIcons.Add)
    data object Favorites : NavItem("favorites", "Favorites", RealEstateIcons.Favorites)
    data object Profile : NavItem("profile", "Profile", RealEstateIcons.Profile)
}
