package com.application.real_estate_app.ui.activities

import android.graphics.drawable.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.w3c.dom.Text

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Profile) // Your destinations
    val currentDestination by navController.currentBackStackEntryAsState()

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surface,
        elevation = 8.dp
    ) {
        items.forEach { screen ->
            val selected = currentDestination?.destination?.route == screen.route
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
            )
        }
    }
}
