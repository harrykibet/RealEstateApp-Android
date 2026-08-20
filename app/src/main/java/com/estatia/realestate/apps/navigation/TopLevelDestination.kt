package com.estatia.realestate.apps.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.estatia.realestate.apps.R
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.navigation.ChatsBaseRoute
import com.estatia.realestate.apps.core.navigation.ChatsRoute
import com.estatia.realestate.apps.core.navigation.HomeBaseRoute
import com.estatia.realestate.apps.core.navigation.HomeRoute
import com.estatia.realestate.apps.core.navigation.ProfileBaseRoute
import com.estatia.realestate.apps.core.navigation.ProfileRoute
import com.estatia.realestate.apps.core.navigation.PropertyBaseRoute
import com.estatia.realestate.apps.core.navigation.PropertyRoute
import kotlin.reflect.KClass

/**
 * Represents a top-level destination in the app. Contains metadata used in the UI and navigation.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
    val showSearch: Boolean = false,
) {
    HOME(
        selectedIcon = EstatiaIcons.Home,
        unselectedIcon = EstatiaIcons.HomeBorder,
        iconTextId = R.string.home,
        titleTextId = R.string.home,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class,
        showSearch = true,
    ),
    ADD_PROPERTY(
        selectedIcon = EstatiaIcons.AddCircle,
        unselectedIcon = EstatiaIcons.AddCircleOutline,
        iconTextId = R.string.add_property,
        titleTextId = R.string.add_property,
        route = PropertyRoute::class,
        baseRoute = PropertyBaseRoute::class,
    ),
    CHATS(
        selectedIcon = EstatiaIcons.Chat,
        unselectedIcon = EstatiaIcons.ChatBorder,
        iconTextId = R.string.chats,
        titleTextId = R.string.chats,
        route = ChatsRoute::class,
        baseRoute = ChatsBaseRoute::class,
    ),
    PROFILE(
        selectedIcon = EstatiaIcons.Profile,
        unselectedIcon = EstatiaIcons.ProfileBorder,
        iconTextId = R.string.profile,
        titleTextId = R.string.profile,
        route = ProfileRoute::class,
        baseRoute = ProfileBaseRoute::class,
    )
}
