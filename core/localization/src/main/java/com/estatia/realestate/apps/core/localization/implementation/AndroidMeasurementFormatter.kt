package com.estatia.realestate.apps.core.localization.implementation

import com.estatia.realestate.apps.core.localization.api.MeasurementFormatter
import java.util.Locale
import javax.inject.Inject

class AndroidMeasurementFormatter @Inject constructor() : MeasurementFormatter {
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
