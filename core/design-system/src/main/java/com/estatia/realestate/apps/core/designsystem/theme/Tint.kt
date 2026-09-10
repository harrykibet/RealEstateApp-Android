package com.estatia.realestate.apps.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * A class to model background color and tonal elevation values for Estatia.
 */
@Immutable
@Helper
data class TintTheme(
    val iconTint: Color = Color.Unspecified,
)

/**
 * A composition local for [TintTheme].
 */
val LocalTintTheme = staticCompositionLocalOf { TintTheme() }
