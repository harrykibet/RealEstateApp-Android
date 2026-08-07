package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.domain.interfaces.ISecretRepository
import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.network.interfaces.ISecretRemoteDataSource
import com.estatia.realestate.apps.core.security.interfaces.SecureKeyProvider
import javax.inject.Inject

/**
 * Mediator repository for secrets.
 * Tries to fetch from remote first, falls back to local provider.
 */
internal class SecretRepository @Inject constructor(
    private val remoteDataSource: ISecretRemoteDataSource,
    private val localProvider: SecureKeyProvider
) : ISecretRepository {

    override suspend fun getSecret(secretId: SecretId): AppResult<String> {
        // 1. Try remote source
        val remoteResult = remoteDataSource.fetchSecret(secretId)
        
        if (remoteResult is AppResult.Success) {
            return remoteResult
        }

        // 2. Fallback to local source (BuildConfig/Keystore)
        val localSecret = localProvider.getLocalSecret(secretId)
        
        return if (localSecret != null) {
            AppResult.Success(localSecret)
        } else {
            // If both fail, return the remote error or a generic local error
            AppResult.Error(SecurityException.InvalidApiKey("Secret not found in any source: ${secretId.value}"))
        }
    }
}
