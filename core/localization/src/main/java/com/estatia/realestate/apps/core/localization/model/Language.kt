package com.estatia.realestate.apps.core.localization.model
import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

/**
 * Represents a supported language in the Estatia app.
 */
@Utility
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
