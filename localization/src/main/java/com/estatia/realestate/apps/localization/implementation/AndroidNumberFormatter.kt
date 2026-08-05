package com.estatia.realestate.apps.localization.implementation

import com.estatia.realestate.apps.localization.api.NumberFormatter
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
}
