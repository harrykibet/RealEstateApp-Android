package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.model.security.SecretId

/**
 * Interface for providing sensitive configuration and API keys.
 * Implementations should retrieve keys from secure locations (e.g. BuildConfig, Keystore, or Backend).
 */
interface SecureKeyProvider {
    /**
     * Retrieves a locally stored secret by its ID.
     * Returns null if the secret is not found locally.
     */
    fun getLocalSecret(secretId: SecretId): String?
}
