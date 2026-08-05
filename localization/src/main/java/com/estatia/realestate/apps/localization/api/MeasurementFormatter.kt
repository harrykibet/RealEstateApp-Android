package com.estatia.realestate.apps.localization.api

/**
 * Interface for localized measurement formatting (e.g., area size, distance).
 */
interface MeasurementFormatter {
    fun formatArea(squareMeters: Double): String
    fun formatDistance(meters: Double): String
}
