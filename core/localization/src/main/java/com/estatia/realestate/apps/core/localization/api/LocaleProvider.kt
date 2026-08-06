package com.estatia.realestate.apps.core.localization.api

import com.estatia.realestate.apps.core.localization.model.SupportedLocale
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

/**
 * Interface for providing current locale information.
 */
interface LocaleProvider {
    val currentLocale: StateFlow<Locale>
    val supportedLocales: List<SupportedLocale>
}
