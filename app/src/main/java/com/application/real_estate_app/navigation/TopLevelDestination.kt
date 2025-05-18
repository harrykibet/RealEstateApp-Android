package com.application.real_estate_app.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.application.real_estate_app.R
import com.application.real_estate_app.feature_home.navigation.HomeRoute
import com.application.real_estate_app.feature_search.navigation.ExploreRoute
import com.application.real_estate_app.feature_profile.navigation.ProfileRoute
import com.application.real_estate_app.feature_favorites.navigation.FavoritesRoute
import com.application.real_estate_app.feature_property.navigation.PropertyRoute
import com.application.real_estate_app.core_design_system.icons.RealEstateIcons
import com.application.real_estate_app.feature_favorites.navigation.FavoritesBaseRoute
import com.application.real_estate_app.feature_home.navigation.HomeBaseRoute
import com.application.real_estate_app.feature_profile.navigation.ProfileBaseRoute
import com.application.real_estate_app.feature_property.navigation.PropertyBaseRoute
import com.application.real_estate_app.feature_search.navigation.ExploreBaseRoute
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
        selectedIcon = RealEstateIcons.Home,
        unselectedIcon = RealEstateIcons.HomeBorder,
        iconTextId = R.string.home,
        titleTextId = R.string.home,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    ),
    EXPLORE(
        selectedIcon = RealEstateIcons.Explore,
        unselectedIcon = RealEstateIcons.ExploreBorder,
        iconTextId = R.string.explore,
        titleTextId = R.string.explore,
        route = ExploreRoute::class,
        baseRoute = ExploreBaseRoute::class
    ),
    ADD_PROPERTY(
        selectedIcon = RealEstateIcons.AddCircle,
        unselectedIcon = RealEstateIcons.AddCircleOutline,
        iconTextId = R.string.add_property,
        titleTextId = R.string.add_property,
        route = PropertyRoute::class,
        baseRoute = PropertyBaseRoute::class
    ),
    FAVORITES(
        selectedIcon = RealEstateIcons.Favorites,
        unselectedIcon = RealEstateIcons.FavoriteBorder,
        iconTextId = R.string.favorites,
        titleTextId = R.string.favorites,
        route = FavoritesRoute::class,
        baseRoute = FavoritesBaseRoute::class
    ),
    PROFILE(
        selectedIcon = RealEstateIcons.Profile,
        unselectedIcon = RealEstateIcons.ProfileBorder,
        iconTextId = R.string.profile,
        titleTextId = R.string.profile,
        route = ProfileRoute::class,
        baseRoute = ProfileBaseRoute::class
    )
}
