package com.estatia.realestate.apps.localization.api

/**
 * Interface for localized currency formatting.
 */
interface CurrencyFormatter {
    fun formatCurrency(amount: Number, currencyCode: String): String
}
