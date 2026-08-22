package com.estatia.realestate.apps.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.designsystem.theme.EstatiaTheme

/**
 * Estatia navigation bar item.
 * Professionalized with minimalist animated content (no pill, scale effect).
 */
@Composable
fun RowScope.EstatiaNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: ImageVector,
    selectedIcon: ImageVector = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            EstatiaNavigationItemContent(
                selected = selected,
                icon = icon,
                selectedIcon = selectedIcon,
            )
        },
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Unspecified, // Controlled by EstatiaNavigationItemContent
            unselectedIconColor = Color.Unspecified,
            selectedTextColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = EstatiaNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
    )
}

/**
 * Estatia navigation bar with content slot. Wraps Material 3 [NavigationBar].
 */
@Composable
fun EstatiaNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = EstatiaNavigationDefaults.navigationContentColor(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Estatia navigation rail item with icon and label content slots.
 */
@Composable
fun EstatiaNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: ImageVector,
    selectedIcon: ImageVector = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = {
            EstatiaNavigationItemContent(
                selected = selected,
                icon = icon,
                selectedIcon = selectedIcon,
            )
        },
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = Color.Unspecified,
            unselectedIconColor = Color.Unspecified,
            selectedTextColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = EstatiaNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
    )
}

/**
 * Estatia navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 */
@Composable
fun EstatiaNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = EstatiaNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * Estatia navigation suite scaffold with item and content slots.
 */
@Composable
fun EstatiaNavigationSuiteScaffold(
    navigationSuiteItems: EstatiaNavigationSuiteScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    layoutType: NavigationSuiteType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo),
    content: @Composable () -> Unit,
) {
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Unspecified,
            unselectedIconColor = Color.Unspecified,
            selectedTextColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = EstatiaNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = Color.Unspecified,
            unselectedIconColor = Color.Unspecified,
            selectedTextColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = EstatiaNavigationDefaults.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = EstatiaNavigationDefaults.navigationContentColor(),
            selectedTextColor = EstatiaNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = EstatiaNavigationDefaults.navigationContentColor(),
        ),
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            EstatiaNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors,
            ).run(navigationSuiteItems)
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = EstatiaNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent,
        ),
        modifier = modifier,
    ) {
        content()
    }
}

/**
 * Shared animated content for navigation items.
 */
@Composable
private fun EstatiaNavigationItemContent(
    selected: Boolean,
    icon: ImageVector,
    selectedIcon: ImageVector,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1.0f,
        label = "NavigationScale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) {
            EstatiaNavigationDefaults.navigationSelectedItemColor()
        } else {
            EstatiaNavigationDefaults.navigationContentColor()
        },
        label = "NavigationColor",
    )

    Box(modifier = Modifier.scale(scale)) {
        Icon(
            imageVector = if (selected) selectedIcon else icon,
            contentDescription = null,
            tint = iconColor,
        )
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class EstatiaNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: ImageVector,
        selectedIcon: ImageVector = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            EstatiaNavigationItemContent(
                selected = selected,
                icon = icon,
                selectedIcon = selectedIcon,
            )
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

@ThemePreviews
@Composable
fun EstatiaNavigationBarPreview() {
    val items = listOf("home", "market", "add", "chats", "profile")
    val icons = listOf(
        EstatiaIcons.HomeBorder,
        EstatiaIcons.MarketBorder,
        EstatiaIcons.AddCircleOutline,
        EstatiaIcons.ChatBorder,
        EstatiaIcons.ProfileBorder,
    )
    val selectedIcons = listOf(
        EstatiaIcons.Home,
        EstatiaIcons.Market,
        EstatiaIcons.AddCircle,
        EstatiaIcons.Chat,
        EstatiaIcons.Profile,
    )

    EstatiaTheme {
        EstatiaNavigationBar {
            items.forEachIndexed { index, item ->
                EstatiaNavigationBarItem(
                    icon = icons[index],
                    selectedIcon = selectedIcons[index],
                    label = { EstatiaText(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Estatia navigation default values.
 */
object EstatiaNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.primary
}
