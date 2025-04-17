package com.application.real_estate_app.ui.activities

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        NavItem.Home,
        NavItem.Explore,
        NavItem.Add,
        NavItem.Favorites,
        NavItem.Profile
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 8.dp
    ) {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentDestination?.route == item.route,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
