package com.estatia.realestate.apps.core.localization.api

import com.estatia.realestate.apps.core.localization.model.SupportedLocale
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Interface for providing current locale information.
 */
@Contract
interface LocaleProvider {
    val currentLocale: StateFlow<Locale>
    val supportedLocales: List<SupportedLocale>
}
