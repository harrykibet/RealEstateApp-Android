package com.estatia.realestate.apps.localization.api

import androidx.annotation.PluralsRes

/**
 * Interface for providing localized plural strings.
 */
interface PluralProvider {
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
}
