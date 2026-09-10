package com.estatia.realestate.apps.core.localization.api

import com.estatia.realestate.apps.core.localization.model.Language
import com.estatia.realestate.apps.core.localization.model.Region
import kotlinx.coroutines.flow.Flow
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for managing and persisting user locale preferences.
 */
@Contract
interface LocaleRepository {
    val selectedLanguage: Flow<Language?>
    val selectedRegion: Flow<Region?>

    suspend fun setLanguage(language: Language)
    suspend fun setRegion(region: Region)
    suspend fun clearPreferences()
}
