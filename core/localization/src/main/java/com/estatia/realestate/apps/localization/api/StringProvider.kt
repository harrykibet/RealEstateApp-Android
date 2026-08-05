package com.estatia.realestate.apps.localization.api

import androidx.annotation.StringRes

/**
 * Interface for providing localized strings.
 */
interface StringProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}
