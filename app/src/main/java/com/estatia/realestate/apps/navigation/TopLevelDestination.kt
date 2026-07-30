package com.estatia.realestate.apps.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.estatia.realestate.apps.R
import com.estatia.realestate.apps.feature.home.navigation.HomeRoute
import com.estatia.realestate.apps.feature.search.navigation.SearchRoute
import com.estatia.realestate.apps.feature.profile.navigation.ProfileRoute
import com.estatia.realestate.apps.feature.favorites.navigation.FavoritesRoute
import com.estatia.realestate.apps.feature.property.navigation.PropertyRoute
import com.estatia.realestate.apps.core.designsystem.icons.EstatiaIcons
import com.estatia.realestate.apps.feature.favorites.navigation.FavoritesBaseRoute
import com.estatia.realestate.apps.feature.home.navigation.HomeBaseRoute
import com.estatia.realestate.apps.feature.profile.navigation.ProfileBaseRoute
import com.estatia.realestate.apps.feature.property.navigation.PropertyBaseRoute
import com.estatia.realestate.apps.feature.search.navigation.SearchBaseRoute
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
) {
    HOME(
        selectedIcon = EstatiaIcons.Home,
        unselectedIcon = EstatiaIcons.HomeBorder,
        iconTextId = R.string.home,
        titleTextId = R.string.home,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class,
    ),
    SEARCH(
        selectedIcon = EstatiaIcons.Search,
        unselectedIcon = EstatiaIcons.SearchBorder,
        iconTextId = R.string.search,
        titleTextId = R.string.search,
        route = SearchRoute::class,
        baseRoute = SearchBaseRoute::class
    ),
    ADD_PROPERTY(
        selectedIcon = EstatiaIcons.AddCircle,
        unselectedIcon = EstatiaIcons.AddCircleOutline,
        iconTextId = R.string.add_property,
        titleTextId = R.string.add_property,
        route = PropertyRoute::class,
        baseRoute = PropertyBaseRoute::class
    ),
    FAVORITES(
        selectedIcon = EstatiaIcons.Favorites,
        unselectedIcon = EstatiaIcons.FavoriteBorder,
        iconTextId = R.string.favorites,
        titleTextId = R.string.favorites,
        route = FavoritesRoute::class,
        baseRoute = FavoritesBaseRoute::class
    ),
    PROFILE(
        selectedIcon = EstatiaIcons.Profile,
        unselectedIcon = EstatiaIcons.ProfileBorder,
        iconTextId = R.string.profile,
        titleTextId = R.string.profile,
        route = ProfileRoute::class,
        baseRoute = ProfileBaseRoute::class
    )
}
