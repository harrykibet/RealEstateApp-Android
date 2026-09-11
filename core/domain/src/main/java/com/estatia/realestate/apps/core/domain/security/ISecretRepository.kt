package com.estatia.realestate.apps.core.domain.security

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Interface for retrieving sensitive API keys and secrets from various sources.
 */
@Contract
interface ISecretRepository {
    /**
     * Fetches a secret by its unique ID.
     * Implementations may try remote sources first and fall back to local ones.
     */
    suspend fun getSecret(secretId: SecretId): AppResult<String>
}
