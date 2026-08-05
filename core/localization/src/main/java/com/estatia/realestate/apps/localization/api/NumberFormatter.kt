package com.estatia.realestate.apps.localization.api

import androidx.compose.runtime.staticCompositionLocalOf

import java.text.NumberFormat
import java.util.Locale

/**
 * Interface for localized number formatting.
 */
interface NumberFormatter {
    fun formatNumber(number: Number): String
    fun formatDecimal(number: Number, decimalPlaces: Int = 2): String
    fun formatPercentage(number: Number): String
    fun formatCompactNumber(number: Number): String
}

/**
 * Global key used to obtain access to the [NumberFormatter] through a CompositionLocal.
 */
val LocalNumberFormatter = staticCompositionLocalOf<NumberFormatter> {
    // Default implementation for previews and cases where it's not provided
    object : NumberFormatter {
        override fun formatNumber(number: Number): String {
            return NumberFormat.getInstance(Locale.getDefault()).format(number)
        }

        override fun formatDecimal(number: Number, decimalPlaces: Int): String {
            val format = NumberFormat.getInstance(Locale.getDefault())
            format.minimumFractionDigits = decimalPlaces
            format.maximumFractionDigits = decimalPlaces
            return format.format(number)
        }

        override fun formatPercentage(number: Number): String {
            return NumberFormat.getPercentInstance(Locale.getDefault()).format(number)
        }

        override fun formatCompactNumber(number: Number): String {
            return when {
                number.toDouble() >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", number.toDouble() / 1_000_000f).replace(".0", "")
                number.toDouble() >= 1_000 -> String.format(Locale.getDefault(), "%.1fk", number.toDouble() / 1_000f).replace(".0", "")
                else -> number.toString()
            }
        }
    }
}
