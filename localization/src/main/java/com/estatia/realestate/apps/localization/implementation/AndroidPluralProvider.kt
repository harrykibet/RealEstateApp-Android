package com.estatia.realestate.apps.localization.implementation

import android.content.Context
import com.estatia.realestate.apps.localization.api.PluralProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidPluralProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : PluralProvider {
    override fun getQuantityString(resId: Int, quantity: Int): String {
        return context.resources.getQuantityString(resId, quantity)
    }

    override fun getQuantityString(resId: Int, quantity: Int, vararg formatArgs: Any): String {
        return context.resources.getQuantityString(resId, quantity, *formatArgs)
    }
}
