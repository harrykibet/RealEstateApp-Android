package com.estatia.realestate.apps.core.localization.model

import java.util.Locale

/**
 * Encapsulates a locale supported by the application.
 */
data class SupportedLocale(
    val language: Language,
    val region: Region? = null
) {
    fun toJavaLocale(): Locale {
        return if (region != null) {
            Locale(language.code, region.code)
        } else {
            Locale(language.code)
        }
    }
}
