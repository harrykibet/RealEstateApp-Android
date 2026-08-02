package com.estatia.realestate.apps.core.security.interfaces

/**
 * Interface for providing sensitive configuration and API keys.
 * Implementations should retrieve keys from secure locations (e.g. BuildConfig, Keystore, or Backend).
 */
interface SecureKeyProvider {
    // Add future keys here (e.g. stripeApiKey, aiServiceKey)
}
