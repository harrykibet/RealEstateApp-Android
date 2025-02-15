package com.application.real_estate_app.core.utils.firebase

import com.application.real_estate_app.core.domain.interfaces.IRemoteConfigManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) : IRemoteConfigManager {
    companion object {
        // Default values for Firebase Remote Config
        private const val DEFAULT_KEYRING_LOCATION_ID = "africa-south1"
        private const val DEFAULT_KEY_RING_ID = "CRYPTO_KEYS"
        private const val DEFAULT_SYMMETRIC_KEY_ID = "GOOGLE_SYMMETRIC_CRYPTO"
        private const val DEFAULT_ASYMMETRIC_KEY_ID = "RSA_CRYPTO"
        private const val DEFAULT_ASYMMETRIC_SIGNING_KEY_ID = "RSA_SIGNING_CRYPTO"

        // Default regex patterns (used if Remote Config values are missing)
        private const val DEFAULT_GOOGLE_KEY_PATTERN = "^AIza[0-9A-Za-z-_]{35}$"
        private const val DEFAULT_GENERIC_KEY_PATTERN = "^[A-Za-z0-9-_]{32,64}$"
        private const val DEFAULT_PAYMENTS_KEY_PATTERN = "^(pk|sk)_(test|live)_[0-9a-zA-Z]{24}$"
    }

    init {
        // Set default values to avoid crashes
        val defaults = mapOf(
            "google_key_pattern" to DEFAULT_GOOGLE_KEY_PATTERN,
            "generic_key_pattern" to DEFAULT_GENERIC_KEY_PATTERN,
            "payments_key_pattern" to DEFAULT_PAYMENTS_KEY_PATTERN,
            "location_id" to DEFAULT_KEYRING_LOCATION_ID,
            "key_ring_id" to DEFAULT_KEY_RING_ID,
            "symmetric_key_id" to DEFAULT_SYMMETRIC_KEY_ID,
            "asymmetric_key_id" to DEFAULT_ASYMMETRIC_KEY_ID,
            "asymmetric_signing_key_id" to DEFAULT_ASYMMETRIC_SIGNING_KEY_ID
        )
        firebaseRemoteConfig.setDefaultsAsync(defaults)

        // Fetch updated values in the background
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Handle failure but do not crash the app
            }
        }
    }

    override fun getGoogleKeyPattern(): String =
        firebaseRemoteConfig.getString("google_key_pattern").takeIf { it.isNotEmpty() }
            ?: DEFAULT_GOOGLE_KEY_PATTERN

    override fun getGenericKeyPattern(): String =
        firebaseRemoteConfig.getString("generic_key_pattern").takeIf { it.isNotEmpty() }
            ?: DEFAULT_GENERIC_KEY_PATTERN

    override fun getPaymentsKeyPattern(): String =
        firebaseRemoteConfig.getString("payments_key_pattern").takeIf { it.isNotEmpty() }
            ?: DEFAULT_PAYMENTS_KEY_PATTERN

    // New methods to fetch encryption-related keys
    override fun getKeyRingLocationId(): String =
        firebaseRemoteConfig.getString("location_id").takeIf { it.isNotEmpty() }
            ?: DEFAULT_KEYRING_LOCATION_ID

    override fun getKeyRingId(): String =
        firebaseRemoteConfig.getString("key_ring_id").takeIf { it.isNotEmpty() }
            ?: DEFAULT_KEY_RING_ID

    override fun getSymmetricKeyId(): String =
        firebaseRemoteConfig.getString("symmetric_key_id").takeIf { it.isNotEmpty() }
            ?: DEFAULT_SYMMETRIC_KEY_ID

    override fun getAsymmetricKeyId(): String =
        firebaseRemoteConfig.getString("asymmetric_key_id").takeIf { it.isNotEmpty() }
            ?: DEFAULT_ASYMMETRIC_KEY_ID

    override fun getAsymmetricSigningKeyId(): String =
        firebaseRemoteConfig.getString("asymmetric_signing_key_id").takeIf { it.isNotEmpty() }
            ?: DEFAULT_ASYMMETRIC_SIGNING_KEY_ID
}
