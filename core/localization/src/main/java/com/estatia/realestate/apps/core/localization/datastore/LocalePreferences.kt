package com.estatia.realestate.apps.core.localization.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locale_prefs")

@Singleton
class LocalePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val languageKey = stringPreferencesKey("language_code")
    private val regionKey = stringPreferencesKey("region_code")

    val languageCode: Flow<String?> = context.dataStore.data.map { it[languageKey] }
    val regionCode: Flow<String?> = context.dataStore.data.map { it[regionKey] }

    suspend fun setLanguageCode(code: String) {
        context.dataStore.edit { it[languageKey] = code }
    }

    suspend fun setRegionCode(code: String) {
        context.dataStore.edit { it[regionKey] = code }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
