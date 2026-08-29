package com.estatia.realestate.apps.core.localization.implementation

import com.estatia.realestate.apps.core.localization.api.NumberFormatter
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class AndroidNumberFormatter @Inject constructor() : NumberFormatter {
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
        val value = number.toDouble()
        val absValue = kotlin.math.abs(value)

        return when {
            absValue >= 1_000_000_000 -> formatUnit(value, 1_000_000_000.0, "B")
            absValue >= 1_000_000 -> formatUnit(value, 1_000_000.0, "M")
            absValue >= 1_000 -> formatUnit(value, 1_000.0, "k")
            else -> number.toLong().toString()
        }
    }

    private fun formatUnit(value: Double, divisor: Double, unit: String): String {
        val formatted = String.format(Locale.getDefault(), "%.1f", value / divisor)
        return if (formatted.endsWith(".0") || formatted.endsWith(",0")) {
            formatted.substring(0, formatted.length - 2) + unit
        } else {
            formatted + unit
        }
    }
}
