package com.application.real_estate_app.ui.activities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Home : NavItem("home", "Home", Icons.Default.Home)
    data object Explore : NavItem("explore", "Explore", Icons.Default.Search)
    data object Add : NavItem("add", "Add", Icons.Default.Add)
    data object Favorites : NavItem("favorites", "Favorites", Icons.Default.Favorite)
    data object Profile : NavItem("profile", "Profile", Icons.Default.Person)
}
