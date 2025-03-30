package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_network.utils.SensitiveString
import com.application.real_estate_app.core_model.CacheKey
import com.application.real_estate_app.core_model.SecretId


interface IGoogleCloudSecretsManager {

    suspend fun getSecret(
        secretId: String,
        version: String,
        context: Map<String, String>
    ): Result<SensitiveString>

    suspend fun preloadSecrets(keys: Set<CacheKey>): Map<CacheKey, Result<SensitiveString>>
    suspend fun evictSecretFromCache(secretId: SecretId)
    suspend fun getLatestStableVersion(secretId: SecretId): String
}