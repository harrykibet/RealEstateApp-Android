package com.application.real_estate_app.security.data.sources.remote

import android.util.Base64
import com.application.real_estate_app.core_interface.IRemoteConfigManager
import com.application.real_estate_app.core_interface.LoggerInterface
import com.application.real_estate_app.security.domain.interfaces.IGoogleCloudKmsManager
import com.application.real_estate_app.security.utils.exceptions.CryptoOperationException
import com.application.real_estate_app.security.utils.exceptions.GoogleKmsException
import com.application.real_estate_app.security.utils.exceptions.InvalidKeyVersionException
import com.google.api.gax.rpc.ApiException
import com.google.cloud.kms.v1.*
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.openssl.PEMParser
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [GoogleCloudKmsManager]
 *
 * This class provides an interface for interacting with Google Cloud Key Management Service (KMS).
 * It supports symmetric and asymmetric encryption/decryption, data signing, and signature verification.
 * It also manages a local cache for public keys to optimize performance.
 *
 * The class is designed to be used as a singleton via Dagger dependency injection.
 *
 * @property kmsClient The Google Cloud KMS client.
 * @property remoteConfig The remote configuration manager for retrieving KMS settings.
 * @property projectId The Google Cloud project ID.
 * @property logger The logger for logging operations and errors.
 *
 * @constructor Creates a [GoogleCloudKmsManager] instance with the provided dependencies.
 */
@Singleton
class GoogleCloudKmsManager @Inject constructor(
    private val kmsClient: KeyManagementServiceClient,
    private val remoteConfig: IRemoteConfigManager,
    private val projectId: String,
    private val logger: LoggerInterface
) : IGoogleCloudKmsManager {

    private sealed interface Algorithm {
        companion object {
            const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
            const val RSA = "RSA"
            const val SHA256_WITH_RSA = "SHA256withRSA"
        }
    }

    private data class CacheKey(
        val keyId: String,
        val keyType: String
    )

    private val publicKeyCache = ConcurrentHashMap<CacheKey, Pair<Long, PublicKey>>()

    private companion object {
        private const val KEY_CACHE_TTL_MINUTES = 5L
        private val KEY_CACHE_TTL_MILLIS = TimeUnit.MINUTES.toMillis(KEY_CACHE_TTL_MINUTES)
        private const val PUBLIC_KEY_SUFFIX = "public"
    }

    // region Public API
    override suspend fun encryptDataSymmetric(plaintext: String): String = executeSecureOperation(
        operation = "symmetric encryption",
        keyType = KeyType.SYMMETRIC,
        preLog = mapOf("dataLength" to plaintext.length)
    ) {
        require(plaintext.isNotEmpty()) { "Plaintext cannot be empty" }
        val ciphertext = kmsClient.encrypt(getKeyName(KeyType.SYMMETRIC), ByteString.copyFromUtf8(plaintext))
        Base64.encodeToString(ciphertext.ciphertext.toByteArray(), Base64.NO_WRAP)
    }

    override suspend fun decryptDataSymmetric(ciphertextBase64: String): String = executeSecureOperation(
        operation = "symmetric decryption",
        keyType = KeyType.SYMMETRIC
    ) {
        val ciphertext = ByteString.copyFrom(Base64.decode(ciphertextBase64, Base64.DEFAULT))
        kmsClient.decrypt(getKeyName(KeyType.SYMMETRIC), ciphertext).plaintext.toStringUtf8()
    }

    override suspend fun encryptDataAsymmetric(plaintext: String): String = executeSecureOperation(
        operation = "asymmetric encryption",
        keyType = KeyType.ASYMMETRIC,
        preLog = mapOf("dataLength" to plaintext.length)
    ) {
        require(plaintext.isNotEmpty()) { "Plaintext cannot be empty" }
        val publicKey = getPublicKey(KeyType.ASYMMETRIC)
        Cipher.getInstance(Algorithm.RSA_TRANSFORMATION).run {
            init(Cipher.ENCRYPT_MODE, publicKey)
            Base64.encodeToString(doFinal(plaintext.toByteArray(StandardCharsets.UTF_8)), Base64.NO_WRAP)
        }
    }

    override suspend fun decryptDataAsymmetric(ciphertextBase64: String): String = executeSecureOperation(
        operation = "asymmetric decryption",
        keyType = KeyType.ASYMMETRIC
    ) {
        kmsClient.asymmetricDecrypt(
            getLatestKeyVersionName(KeyType.ASYMMETRIC),
            ByteString.copyFrom(Base64.decode(ciphertextBase64, Base64.DEFAULT))
        ).plaintext.toStringUtf8()
    }

    override suspend fun signData(data: String): String = executeSecureOperation(
        operation = "data signing",
        keyType = KeyType.ASYMMETRIC_SIGNING
    ) {
        require(data.isNotEmpty()) { "Plaintext cannot be empty" }
        val signResponse = kmsClient.asymmetricSign(
            getLatestKeyVersionName(KeyType.ASYMMETRIC_SIGNING),
            Digest.newBuilder().setSha256(ByteString.copyFromUtf8(data)).build()
        )
        Base64.encodeToString(signResponse.signature.toByteArray(), Base64.NO_WRAP)
    }

    override suspend fun verifySignature(data: String, signatureBase64: String): Boolean = executeSecureOperation(
        operation = "signature verification",
        keyType = KeyType.ASYMMETRIC_SIGNING
    ) {
        val publicKey = getPublicKey(KeyType.ASYMMETRIC_SIGNING)
        val signature = Base64.decode(signatureBase64, Base64.DEFAULT)

        Signature.getInstance(Algorithm.SHA256_WITH_RSA).run {
            initVerify(publicKey)
            update(data.toByteArray(StandardCharsets.UTF_8))
            verify(signature)
        }
    }

    override suspend fun listKeys(): List<String> = executeSecureOperation(
        operation = "key listing",
        keyType = KeyType.ANY
    ) {
        kmsClient.listCryptoKeys(getKeyRingName())
            .iterateAll()
            .map { it.name }
            .also { logger.d("Listed ${it.size} keys") }
    }
    // endregion

    // region Core Implementation
    private suspend fun <T> executeSecureOperation(
        operation: String,
        keyType: KeyType,
        preLog: Map<String, Any>? = null,
        block: suspend () -> T
    ): T = withContext(Dispatchers.IO) {
        try {
            logger.d(buildLogMessage("Starting $operation", preLog, keyType))
            val result = block()
            logger.d(buildLogMessage("Completed $operation", mapOf("result" to (result?.let { it::class.simpleName } ?: "null")), keyType))
            result
        } catch (e: ApiException) {
            logger.e("KMS operation failed: $operation - ${e.message}")
            throw GoogleKmsException("KMS operation failed: ${e.message}", e)
        } catch (e: GeneralSecurityException) {
            logger.e("Security exception in $operation - ${e.message}")
            throw CryptoOperationException("Security operation failed", e)
        } catch (e: IllegalArgumentException) {
            logger.e("Invalid argument in $operation - ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.e("Unexpected error in $operation - ${e.message}")
            throw CryptoOperationException("Operation failed: $operation", e)
        }
    }

    private fun buildLogMessage(base: String, metadata: Map<String, Any>?, keyType: KeyType): String {
        val keyId = getKeyId(keyType)
        return listOfNotNull(
            base,
            metadata?.entries?.joinToString(", ") { "${it.key}=${it.value}" },
            "keyId=$keyId"
        ).joinToString(" | ")
    }

    private suspend fun getPublicKey(keyType: KeyType): PublicKey = withContext(Dispatchers.IO) {
        val cacheKey = CacheKey(getKeyId(keyType), PUBLIC_KEY_SUFFIX)
        val now = System.currentTimeMillis()

        publicKeyCache[cacheKey]?.let { (timestamp, key) ->
            if (now - timestamp < KEY_CACHE_TTL_MILLIS) {
                logger.d("Using cached public key for ${cacheKey.keyId}")
                key
            } else {
                logger.d("Cache expired for ${cacheKey.keyId}")
                fetchAndCachePublicKey(cacheKey)
            }
        } ?: fetchAndCachePublicKey(cacheKey)
    }

    private suspend fun fetchAndCachePublicKey(cacheKey: CacheKey): PublicKey {
        val keyVersionName = getLatestKeyVersionName(KeyType.fromCacheKey(cacheKey))
        val publicKeyResponse = kmsClient.getPublicKey(keyVersionName)

        return parsePublicKey(publicKeyResponse.pem).also {
            publicKeyCache[cacheKey] = System.currentTimeMillis() to it
            logger.d("Refreshed public key cache for ${cacheKey.keyId}")
        }
    }

    private fun parsePublicKey(pem: String): PublicKey {
        return PEMParser(StringReader(pem)).use { parser ->
            val subjectPublicKeyInfo = parser.readObject() as? SubjectPublicKeyInfo
                ?: throw IllegalStateException("Invalid PEM format")

            KeyFactory.getInstance(Algorithm.RSA).generatePublic(
                X509EncodedKeySpec(subjectPublicKeyInfo.encoded)
            )
        }
    }
    // endregion

    // region Key Management Utilities
    private enum class KeyType {
        SYMMETRIC, ASYMMETRIC, ASYMMETRIC_SIGNING, ANY;

        companion object {
            fun fromCacheKey(cacheKey: CacheKey): KeyType = when {
                cacheKey.keyId.endsWith("-sign") -> ASYMMETRIC_SIGNING
                cacheKey.keyId.startsWith("asym-") -> ASYMMETRIC
                else -> SYMMETRIC
            }
        }
    }

    private fun getKeyId(keyType: KeyType): String = when (keyType) {
        KeyType.SYMMETRIC -> remoteConfig.getSymmetricKeyId()
        KeyType.ASYMMETRIC -> remoteConfig.getAsymmetricKeyId()
        KeyType.ASYMMETRIC_SIGNING -> remoteConfig.getAsymmetricSigningKeyId()
        KeyType.ANY -> "any"
    }

    private fun getKeyRingName() = KeyRingName.of(
        projectId,
        remoteConfig.getKeyRingLocationId(),
        remoteConfig.getKeyRingId()
    )

    private fun getKeyName(keyType: KeyType) = CryptoKeyName.of(
        projectId,
        remoteConfig.getKeyRingLocationId(),
        remoteConfig.getKeyRingId(),
        getKeyId(keyType)
    )

    private suspend fun getLatestKeyVersionName(keyType: KeyType): CryptoKeyVersionName =
        withContext(Dispatchers.IO) {
            kmsClient.listCryptoKeyVersions(getKeyName(keyType))
                .iterateAll()
                .firstOrNull { it.state == CryptoKeyVersion.CryptoKeyVersionState.ENABLED }
                ?.name
                ?.let { CryptoKeyVersionName.parse(it) }
                ?: throw InvalidKeyVersionException(
                    "No enabled versions found for ${getKeyId(keyType)}")
        }
    // endregion
}