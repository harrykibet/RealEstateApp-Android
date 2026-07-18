package com.estatia.realestate.apps.core.security

import android.content.SharedPreferences
import androidx.core.content.edit
import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenLocalDataSource(
    private val encryptedPrefs: SharedPreferences
) : ITokenLocalDataSource {

    private companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit { putString(KEY_AUTH_TOKEN, token) }
        AppResult.Success(Unit)
    }

    override suspend fun getToken() = withContext(Dispatchers.IO) {
        AppResult.Success(encryptedPrefs.getString(KEY_AUTH_TOKEN, null))
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit { remove(KEY_AUTH_TOKEN) }
        AppResult.Success(Unit)
    }
}