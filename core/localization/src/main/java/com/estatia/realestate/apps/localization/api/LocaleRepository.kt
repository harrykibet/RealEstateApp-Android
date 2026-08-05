package com.estatia.realestate.apps.localization.api

import com.estatia.realestate.apps.localization.model.Language
import com.estatia.realestate.apps.localization.model.Region
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing and persisting user locale preferences.
 */
interface LocaleRepository {
    val selectedLanguage: Flow<Language?>
    val selectedRegion: Flow<Region?>

    suspend fun setLanguage(language: Language)
    suspend fun setRegion(region: Region)
    suspend fun clearPreferences()
}
