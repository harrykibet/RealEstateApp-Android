package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.utils.SensitiveString
import com.estatia.realestate.apps.core.model.security.CacheKey
import com.estatia.realestate.apps.core.model.security.SecretId


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