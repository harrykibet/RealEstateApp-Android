package com.application.real_estate_app.security.domain.interfaces

import com.application.real_estate_app.security.domain.models.CacheKey
import com.application.real_estate_app.security.domain.models.SecretId
import com.application.real_estate_app.security.utils.extensions.SensitiveString


interface IGoogleSecretsManager {

    suspend fun getSecret(
        secretId: String,
        version: String,
        context: Map<String, String>
    ): Result<SensitiveString>

    suspend fun preloadSecrets(keys: Set<CacheKey>): Map<CacheKey, Result<SensitiveString>>
    suspend fun evictSecretFromCache(secretId: SecretId)
    suspend fun getLatestStableVersion(secretId: SecretId): String
}