package com.estatia.realestate.apps.core.localization.model
import com.estatia.realestate.apps.core.architecture.annotations.Helper

/**
 * Represents a supported language in the Estatia app.
 */
@Helper
data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

val SupportedLanguages = listOf(
    Language("en", "English", "English"),
    Language("sw", "Swahili", "Kiswahili"),
    Language("fr", "French", "Français"),
    Language("es", "Spanish", "Español")
)
