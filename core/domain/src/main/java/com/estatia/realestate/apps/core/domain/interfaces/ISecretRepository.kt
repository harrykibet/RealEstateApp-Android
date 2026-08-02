package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.security.SecretId

/**
 * Interface for retrieving sensitive API keys and secrets from various sources.
 */
interface ISecretRepository {
    /**
     * Fetches a secret by its unique ID.
     * Implementations may try remote sources first and fall back to local ones.
     */
    suspend fun getSecret(secretId: SecretId): AppResult<String>
}
