package com.estatia.realestate.apps.core.security

import com.estatia.realestate.apps.core.security.interfaces.SecureKeyProvider
import javax.inject.Inject

/**
 * Implementation of [SecureKeyProvider] that retrieves keys from injected [BuildConfig] fields.
 */
class BuildConfigSecureKeyProvider @Inject constructor() : SecureKeyProvider {
    // Implement future key getters here
}
