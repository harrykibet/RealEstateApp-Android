package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for fetching secrets from a remote backend (e.g. Google Cloud Secret Manager).
 */
@Contract
interface ISecretRemoteDataSource {
    suspend fun fetchSecret(secretId: SecretId): AppResult<String>
}
