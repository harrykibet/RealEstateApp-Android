package com.estatia.realestate.apps.core.security

import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-integrity local storage for authentication tokens.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage the secure persistence of identity tokens using hardware-backed encryption.
 * - Security: Every token is encrypted with AES-GCM before being saved to Disk (DataStore).
 * - Concurrency: Thread-safe via [withContext] Dispatchers.IO.
 * - Resilience: Implements a one-way migration path from legacy plain-text storage to encrypted storage.
 * - Observability: Tracks token encryption and decryption failure rates.
 */
@Singleton
class TokenLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoEngine: IAesGcmCryptoEngine,
    private val metricsTracker: IMetricsTracker
) : ITokenLocalDataSource {

    private companion object {
        // "auth_token" is the key used in the old EncryptedSharedPreferences.
        // SharedPreferencesMigration will move it to DataStore as is (plain text).
        val KEY_MIGRATED_TOKEN = stringPreferencesKey("auth_token")
        val KEY_SECURE_AUTH_TOKEN = stringPreferencesKey("secure_auth_token")
        const val DELIMITER = "|"
    }

    override suspend fun saveToken(token: String): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val encodedValue = encryptToken(token)
            dataStore.edit { preferences ->
                preferences[KEY_SECURE_AUTH_TOKEN] = encodedValue
                // Ensure the migrated plain token is removed if it exists
                preferences.remove(KEY_MIGRATED_TOKEN)
            }
            metricsTracker.incrementCounter("security.token.save_success")
            AppResult.Success(Unit)
        } catch (e: Exception) {
            metricsTracker.incrementCounter("security.token.save_failure")
            AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.SecurityException.EncryptionFailed(e))
        }
    }

    override suspend fun getToken(): AppResult<String?> = withContext(Dispatchers.IO) {
        try {
            val preferences = dataStore.data.firstOrNull() ?: return@withContext AppResult.Success(null)
            
            val secureToken = preferences[KEY_SECURE_AUTH_TOKEN]
            if (secureToken != null) {
                val token = decryptToken(secureToken)
                metricsTracker.incrementCounter("security.token.get_success")
                return@withContext AppResult.Success(token)
            }

            // Fallback to migrated plain token
            val migratedToken = preferences[KEY_MIGRATED_TOKEN]
            if (migratedToken != null) {
                // Secure it now for future uses
                saveToken(migratedToken)
                metricsTracker.incrementCounter("security.token.migration_success")
                return@withContext AppResult.Success(migratedToken)
            }

            AppResult.Success(null)
        } catch (e: Exception) {
            metricsTracker.incrementCounter("security.token.get_failure")
            AppResult.Error(com.estatia.realestate.apps.core.common.exceptions.SecurityException.DecryptionFailed(e))
        }
    }

    override suspend fun clearToken(): AppResult<Unit> = withContext(Dispatchers.IO) {
        dataStore.edit { preferences ->
            preferences.remove(KEY_SECURE_AUTH_TOKEN)
            preferences.remove(KEY_MIGRATED_TOKEN)
        }
        AppResult.Success(Unit)
    }

    private suspend fun encryptToken(token: String): String {
        val encryptedPayload = cryptoEngine.encrypt(token.toByteArray()).getOrThrow()
        val ivBase64 = Base64.encodeToString(encryptedPayload.iv, Base64.NO_WRAP)
        val ciphertextBase64 = Base64.encodeToString(encryptedPayload.ciphertext, Base64.NO_WRAP)
        return "$ivBase64$DELIMITER$ciphertextBase64"
    }

    private suspend fun decryptToken(encodedValue: String): String {
        val parts = encodedValue.split(DELIMITER)
        if (parts.size != 2) throw com.estatia.realestate.apps.core.common.exceptions.SecurityException.DecryptionFailed()

        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)

        val payload = EncryptedPayload(version = 1, iv = iv, ciphertext = ciphertext)
        val decryptedData = cryptoEngine.decrypt(payload).getOrThrow()
        return String(decryptedData)
    }
}
