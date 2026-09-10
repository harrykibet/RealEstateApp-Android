package com.estatia.realestate.apps.core.localization.api

import androidx.annotation.PluralsRes
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for providing localized plural strings.
 */
@Contract
interface PluralProvider {
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg formatArgs: Any): String
}
