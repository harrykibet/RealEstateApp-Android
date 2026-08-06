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
        return when {
            number.toDouble() >= 1_000_000 -> String.format(Locale.getDefault(), "%.1fM", number.toDouble() / 1_000_000f).replace(".0", "")
            number.toDouble() >= 1_000 -> String.format(Locale.getDefault(), "%.1fk", number.toDouble() / 1_000f).replace(".0", "")
            else -> number.toString()
        }
    }
}
