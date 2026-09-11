package com.estatia.realestate.apps.core.localization.implementation

import com.estatia.realestate.apps.core.localization.api.CurrencyFormatter
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import com.estatia.realestate.apps.core.architecture.annotations.Foundation

@Foundation
class AndroidCurrencyFormatter @Inject constructor() : CurrencyFormatter {
    override fun formatCurrency(amount: Number, currencyCode: String): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        return format.format(amount)
    }
}
