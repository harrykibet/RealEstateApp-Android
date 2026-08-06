package com.estatia.realestate.apps.core.localization.api

import androidx.compose.runtime.staticCompositionLocalOf

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Interface for localized currency formatting.
 */
interface CurrencyFormatter {
    fun formatCurrency(amount: Number, currencyCode: String): String
}

/**
 * Global key used to obtain access to the [CurrencyFormatter] through a CompositionLocal.
 */
val LocalCurrencyFormatter = staticCompositionLocalOf<CurrencyFormatter> {
    // Default implementation for previews and cases where it's not provided
    object : CurrencyFormatter {
        override fun formatCurrency(amount: Number, currencyCode: String): String {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance(currencyCode)
            return format.format(amount)
        }
    }
}
