package com.estatia.realestate.apps.localization.api

/**
 * Interface for localized number formatting.
 */
interface NumberFormatter {
    fun formatNumber(number: Number): String
    fun formatDecimal(number: Number, decimalPlaces: Int = 2): String
    fun formatPercentage(number: Number): String
}
