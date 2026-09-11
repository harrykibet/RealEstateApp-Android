package com.estatia.realestate.apps.core.security

import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.security.interfaces.ISecureKeyProvider
import javax.inject.Inject
import com.estatia.realestate.apps.core.architecture.annotations.Foundation

/**
 * Implementation of [ISecureKeyProvider] that retrieves hardcoded keys from [BuildConfig].
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Act as the final fallback for application secrets when remote lookup fails.
 * - Security: Provides keys injected during the build process; assumes the build environment is secure.
 * - Concurrency: Stateless and thread-safe.
 */
@Foundation
class BuildConfigSecureKeyProvider @Inject constructor() : ISecureKeyProvider {
    override fun getLocalSecret(secretId: SecretId): String? {
        // Map SecretId to BuildConfig fields
        return when (secretId.value) {
            // Example mapping:
            // "stripe-api-key" -> BuildConfig.STRIPE_API_KEY
            else -> null
        }
    }
}
