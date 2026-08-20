package com.estatia.realestate.apps.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.estatia.realestate.apps.R
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.core.navigation.routes.ChatsBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.ChatsRoute
import com.estatia.realestate.apps.core.navigation.routes.MarketBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.MarketRoute
import com.estatia.realestate.apps.core.navigation.routes.HomeBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.HomeRoute
import com.estatia.realestate.apps.core.navigation.routes.ProfileBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.ProfileRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyBaseRoute
import com.estatia.realestate.apps.core.navigation.routes.PropertyRoute
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
    MARKET(
        selectedIcon = EstatiaIcons.Market,
        unselectedIcon = EstatiaIcons.MarketBorder,
        iconTextId = R.string.market,
        titleTextId = R.string.market,
        route = MarketRoute::class,
        baseRoute = MarketBaseRoute::class,
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
