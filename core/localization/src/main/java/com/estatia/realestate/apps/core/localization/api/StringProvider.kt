package com.estatia.realestate.apps.core.localization.api

import androidx.annotation.StringRes
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Interface for providing localized strings.
 */
@Contract
interface StringProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}
