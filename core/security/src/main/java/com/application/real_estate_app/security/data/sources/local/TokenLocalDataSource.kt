package com.application.real_estate_app.security.data.sources.local

import android.content.SharedPreferences
import com.application.real_estate_app.core_common.errors.Result
import com.application.real_estate_app.security.domain.interfaces.ITokenLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenLocalDataSource(
    private val encryptedPrefs: SharedPreferences
) : ITokenLocalDataSource {

    private companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
        Result.Success(Unit)
    }

    override suspend fun getToken() = withContext(Dispatchers.IO) {
        Result.Success(encryptedPrefs.getString(KEY_AUTH_TOKEN, null))
    }

    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().remove(KEY_AUTH_TOKEN).apply()
        Result.Success(Unit)
    }
}