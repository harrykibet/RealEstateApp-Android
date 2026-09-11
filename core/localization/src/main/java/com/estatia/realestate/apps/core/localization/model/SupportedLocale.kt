package com.estatia.realestate.apps.core.localization.model

import java.util.Locale
import com.estatia.realestate.apps.core.architecture.annotations.Utility

/**
 * Encapsulates a locale supported by the application.
 */
@Utility
data class SupportedLocale(
    val language: Language,
    val region: Region? = null
) {
    fun toJavaLocale(): Locale {
        return if (region != null) {
            Locale.Builder()
                .setLanguage(language.code)
                .setRegion(region.code)
                .build()
        } else {
            Locale.Builder()
                .setLanguage(language.code)
                .build()
        }
    }
}
