package com.estatia.realestate.apps.core.localization.implementation

import com.estatia.realestate.apps.core.localization.api.LocaleRepository
import com.estatia.realestate.apps.core.localization.datastore.LocalePreferences
import com.estatia.realestate.apps.core.localization.model.Language
import com.estatia.realestate.apps.core.localization.model.Region
import com.estatia.realestate.apps.core.localization.model.SupportedLanguages
import com.estatia.realestate.apps.core.localization.model.SupportedRegions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocaleRepositoryImpl @Inject constructor(
    private val localePreferences: LocalePreferences
) : LocaleRepository {

    override val selectedLanguage: Flow<Language?> = localePreferences.languageCode.map { code ->
        SupportedLanguages.find { it.code == code }
    }

    override val selectedRegion: Flow<Region?> = localePreferences.regionCode.map { code ->
        SupportedRegions.find { it.code == code }
    }

    override suspend fun setLanguage(language: Language) {
        localePreferences.setLanguageCode(language.code)
    }

    override suspend fun setRegion(region: Region) {
        localePreferences.setRegionCode(region.code)
    }

    override suspend fun clearPreferences() {
        localePreferences.clear()
    }
}
