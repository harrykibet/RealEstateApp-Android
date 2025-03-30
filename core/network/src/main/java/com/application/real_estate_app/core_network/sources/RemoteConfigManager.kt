package com.application.real_estate_app.core_network.sources

import android.content.Context
import com.application.real_estate_app.core_domain.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor(
    private val context: Context,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val logger: LoggerInterface
) : IRemoteConfigManager {

    companion object {
        private const val REMOTE_CONFIG_KEY = "firebase_remote_config"
    }

    private var remoteConfigJson: JSONObject = loadLocalJsonConfig()

    init {
        firebaseRemoteConfig.setDefaultsAsync(mapOf(REMOTE_CONFIG_KEY to remoteConfigJson.toString()))
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val jsonString =
                    firebaseRemoteConfig.getString(REMOTE_CONFIG_KEY).takeIf { it.isNotEmpty() }
                        ?: remoteConfigJson.toString()
                remoteConfigJson = JSONObject(jsonString)
                logger.d("Remote Config updated successfully.")
            } else {
                logger.e("Firebase Remote Config fetch error")
            }
        }
    }

    private fun loadLocalJsonConfig(): JSONObject {
        return try {
            val inputStream = context.assets.open("remote_config_defaults.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val jsonText = bufferedReader.use { it.readText() }
            JSONObject(jsonText)
        } catch (e: Exception) {
            logger.e("Failed to load local JSON config", e)
            JSONObject()
        }
    }

    override fun getGoogleKeyPattern(): String =
        remoteConfigJson.optJSONObject("key_patterns")
            ?.optString("google", "^AIza[0-9A-Za-z-_]{35}$") ?: "^AIza[0-9A-Za-z-_]{35}$"

    override fun getGenericKeyPattern(): String =
        remoteConfigJson.optJSONObject("key_patterns")
            ?.optString("generic", "^[A-Za-z0-9-_]{32,64}$") ?: "^[A-Za-z0-9-_]{32,64}$"

    override fun getPaymentsKeyPattern(): String =
        remoteConfigJson.optJSONObject("key_patterns")
            ?.optString("payments", "^(pk|sk)_(test|live)_[0-9a-zA-Z]{24}$")
            ?: "^(pk|sk)_(test|live)_[0-9a-zA-Z]{24}$"

    override fun getKeyRingLocationId(): String =
        remoteConfigJson.optJSONObject("encryption_keys")?.optString("location_id", "africa-south1")
            ?: "africa-south1"

    override fun getKeyRingId(): String =
        remoteConfigJson.optJSONObject("encryption_keys")?.optString("key_ring_id", "CRYPTO_KEYS")
            ?: "CRYPTO_KEYS"

    override fun getSymmetricKeyId(): String =
        remoteConfigJson.optJSONObject("encryption_keys")
            ?.optString("symmetric_key_id", "GOOGLE_SYMMETRIC_CRYPTO") ?: "GOOGLE_SYMMETRIC_CRYPTO"

    override fun getAsymmetricKeyId(): String =
        remoteConfigJson.optJSONObject("encryption_keys")
            ?.optString("asymmetric_key_id", "RSA_CRYPTO") ?: "RSA_CRYPTO"

    override fun getAsymmetricSigningKeyId(): String =
        remoteConfigJson.optJSONObject("encryption_keys")
            ?.optString("asymmetric_signing_key_id", "RSA_SIGNING_CRYPTO") ?: "RSA_SIGNING_CRYPTO"

    override fun getCDNEndPoint1(): String =
        remoteConfigJson.optJSONObject("cdn_endpoints")
            ?.optString("endpoint1", "https://cdn1.example.com") ?: "https://cdn1.example.com"

    override fun getCDNEndPoint2(): String =
        remoteConfigJson.optJSONObject("cdn_endpoints")
            ?.optString("endpoint2", "https://cdn2.example.com") ?: "https://cdn2.example.com"

    override fun getBaseUrl(): String =
        remoteConfigJson.optJSONObject("base_config")
            ?.optString("base_url", "https://firestore-72e4c.firebaseapp.com")
            ?: "https://firestore-72e4c.firebaseapp.com"

    override fun getEnableLogging(): Boolean =
        remoteConfigJson.optJSONObject("base_config")?.optBoolean("enable_logging", true) ?: true
}
