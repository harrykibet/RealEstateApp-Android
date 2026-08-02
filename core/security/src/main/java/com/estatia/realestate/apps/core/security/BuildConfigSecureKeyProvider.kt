package com.estatia.realestate.apps.core.security

import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.security.interfaces.SecureKeyProvider
import javax.inject.Inject

/**
 * Implementation of [SecureKeyProvider] that retrieves keys from injected [BuildConfig] fields.
 */
class BuildConfigSecureKeyProvider @Inject constructor() : SecureKeyProvider {
    override fun getLocalSecret(secretId: SecretId): String? {
        // Map SecretId to BuildConfig fields
        return when (secretId.value) {
            // Example mapping:
            // "stripe-api-key" -> BuildConfig.STRIPE_API_KEY
            else -> null
        }
    }
}
