package com.estatia.realestate.apps.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.estatia.realestate.apps.core.domain.interfaces.IExoplayer

/**
 * CompositionLocal for [IExoplayer]. Must be provided by the app (e.g. in MainActivity)
 * so that screens like FavoritesScreen can use the shared player without direct injection.
 */
val LocalIExoplayer = staticCompositionLocalOf<IExoplayer> {
    error("No IExoplayer provided. Provide it via CompositionLocalProvider in your root composable.")
}
