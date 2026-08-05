package com.estatia.realestate.apps.localization.api

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * Interface for localized measurement formatting (e.g., area size, distance).
 */
interface MeasurementFormatter {
    fun formatArea(squareMeters: Double): String
    fun formatDistance(meters: Double): String
}

/**
 * Global key used to obtain access to the [MeasurementFormatter] through a CompositionLocal.
 */
val LocalMeasurementFormatter = staticCompositionLocalOf<MeasurementFormatter> {
    // Default implementation for previews and cases where it's not provided
    object : MeasurementFormatter {
        override fun formatArea(squareMeters: Double): String {
            return if (Locale.getDefault().country == "US") {
                val sqft = squareMeters * 10.7639
                String.format(Locale.getDefault(), "%.0f sq ft", sqft)
            } else {
                String.format(Locale.getDefault(), "%.1f m²", squareMeters)
            }
        }

        override fun formatDistance(meters: Double): String {
            return if (meters >= 1000) {
                String.format(Locale.getDefault(), "%.1f km", meters / 1000)
            } else {
                String.format(Locale.getDefault(), "%.0f m", meters)
            }
        }
    }
}
