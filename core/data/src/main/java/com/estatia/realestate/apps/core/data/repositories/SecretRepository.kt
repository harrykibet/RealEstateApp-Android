package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.domain.security.ISecretRepository
import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.network.interfaces.ISecretRemoteDataSource
import com.estatia.realestate.apps.core.security.interfaces.SecureKeyProvider
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import javax.inject.Inject

/**
 * Mediator repository for managing application secrets and API keys.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Resolving secrets via a hierarchical lookup (Remote -> Local).
 * - Security: Ensures secrets are never persisted in plain text on the local filesystem.
 * - Concurrency: Stateless and thread-safe.
 * - Resilience: Transparent fallback to [localProvider] (keystore/buildConfig) when network is unavailable.
 * - Observability: Tracks secret resolution funnel (Remote hit vs Local fallback).
 */
internal class SecretRepository @Inject constructor(
    private val remoteDataSource: ISecretRemoteDataSource,
    private val localProvider: SecureKeyProvider,
    private val metricsTracker: IMetricsTracker
) : ISecretRepository {

    override suspend fun getSecret(secretId: SecretId): AppResult<String> {
        // 1. Try remote source
        val remoteResult = remoteDataSource.fetchSecret(secretId)
        
        if (remoteResult is AppResult.Success) {
            metricsTracker.incrementCounter("security.secrets.remote_hit")
            return remoteResult
        }

        // 2. Fallback to local source (BuildConfig/Keystore)
        val localSecret = localProvider.getLocalSecret(secretId)
        
        return if (localSecret != null) {
            metricsTracker.incrementCounter("security.secrets.local_fallback")
            AppResult.Success(localSecret)
        } else {
            metricsTracker.incrementCounter("security.secrets.total_failure")
            // If both fail, return the remote error or a generic local error
            AppResult.Error(SecurityException.InvalidApiKey("Secret not found in any source: ${secretId.value}"))
        }
    }
}
