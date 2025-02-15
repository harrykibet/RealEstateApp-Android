package com.application.real_estate_app.security.data.sources.remote

import android.util.Base64
import com.application.real_estate_app.core.domain.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.security.domain.interfaces.IGoogleCloudKmsManager
import com.application.real_estate_app.security.utils.exceptions.CryptoOperationException
import com.application.real_estate_app.security.utils.exceptions.GoogleKmsException
import com.application.real_estate_app.security.utils.exceptions.InvalidKeyVersionException
import com.google.api.gax.rpc.ApiException
import com.google.cloud.kms.v1.*
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.openssl.PEMParser
import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleCloudKmsManager @Inject constructor(
    private val kmsClient: KeyManagementServiceClient,
    private val remoteConfig: IRemoteConfigManager,
    private val projectId: String,
    private val logger: LoggerInterface
) : IGoogleCloudKmsManager {

    companion object {
        private const val RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private const val KEY_CACHE_TTL_MINUTES = 5L
        private val KEY_CACHE_TTL_MILLIS = TimeUnit.MINUTES.toMillis(KEY_CACHE_TTL_MINUTES)
    }

    private val publicKeyCache = mutableMapOf<String, Pair<Long, java.security.PublicKey>>()

    override suspend fun encryptDataSymmetric(plaintext: String): String =
        withContext(Dispatchers.IO) {
        require(plaintext.isNotEmpty()) { "Plaintext cannot be empty" }

            return@withContext try {
                val keyName = getSymmetricKeyName()

                logger.d(
                    "Starting symmetric encryption" +
                            mapOf("keyId" to remoteConfig.getSymmetricKeyId(), "dataLength" to plaintext.length)
                )

                val encryptResponse = kmsClient.encrypt(keyName, ByteString.copyFrom(plaintext.toByteArray(StandardCharsets.UTF_8)))
                val cipherText = Base64.encodeToString(encryptResponse.ciphertext.toByteArray(), Base64.NO_WRAP)

                logger.d(
                    "Completed symmetric encryption" +
                            mapOf("keyId" to remoteConfig.getSymmetricKeyId(), "ciphertextLength" to cipherText.length)
                )

                cipherText
            } catch (e: ApiException) {
                logger.e("Symmetric encryption failed", e)
                throw GoogleKmsException("KMS operation failed: ${e.message}", e)
            } catch (e: Exception) {
                logger.e("Unexpected error in symmetric encryption", e)
                throw CryptoOperationException("Encryption failed", e)
            }
    }

    override suspend fun decryptDataSymmetric(ciphertextBase64: String): String =
        withContext(Dispatchers.IO) {
        require(ciphertextBase64.isNotEmpty()) { "Ciphertext cannot be empty" }

            return@withContext try {
                val keyName = getSymmetricKeyName()

                logger.d("Starting symmetric decryption" +
                            mapOf("keyId" to remoteConfig.getSymmetricKeyId()))

                val ciphertext = ByteString.copyFrom(Base64.decode(ciphertextBase64, Base64.DEFAULT))
                val decryptResponse = kmsClient.decrypt(keyName, ciphertext)
                val plaintext = decryptResponse.plaintext.toString(StandardCharsets.UTF_8)

                logger.d("Completed symmetric decryption" +
                            mapOf("keyId" to remoteConfig.getSymmetricKeyId(), "dataLength" to plaintext.length))

                plaintext
            } catch (e: ApiException) {
                logger.e("Symmetric decryption failed", e)
                throw GoogleKmsException("KMS operation failed: ${e.message}", e)
            } catch (e: Exception) {
                logger.e("Unexpected error in symmetric decryption", e)
                throw CryptoOperationException("Decryption failed", e)
            }
    }

    override suspend fun encryptDataAsymmetric(plaintext: String): String =
        withContext(Dispatchers.IO) {
        require(plaintext.isNotEmpty()) { "Plaintext cannot be empty" }

        return@withContext try {
            val publicKey = getPublicKey()
            val cipher = Cipher.getInstance(RSA_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))

            logger.d("Completed asymmetric encryption" +
                        mapOf("keyId" to remoteConfig.getAsymmetricKeyId(), "dataLength" to plaintext.length))

            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: GeneralSecurityException) {
            logger.e("Asymmetric encryption failed", e)
            throw CryptoOperationException("Encryption operation failed", e)
        } catch (e: Exception) {
            logger.e("Unexpected error in asymmetric encryption", e)
            throw CryptoOperationException("Encryption failed", e)
        }
    }

    override suspend fun decryptDataAsymmetric(ciphertextBase64: String): String =
        withContext(Dispatchers.IO){
        require(ciphertextBase64.isNotEmpty()) { "Ciphertext cannot be empty" }

            return@withContext try {
                val keyVersionName = getLatestAsymmetricKeyVersion()
                logger.d(
                    "Starting asymmetric decryption" + mapOf("keyVersion" to keyVersionName))

                val ciphertext = ByteString.copyFrom(Base64.decode(ciphertextBase64, Base64.DEFAULT))
                val decryptResponse = kmsClient.asymmetricDecrypt(keyVersionName, ciphertext)

                logger.d(
                    "Completed asymmetric decryption" + mapOf("keyVersion" to keyVersionName))

                decryptResponse.plaintext.toString(StandardCharsets.UTF_8)
            } catch (e: ApiException) {
                logger.e("Asymmetric decryption failed", e)
                throw GoogleKmsException("KMS operation failed: ${e.message}", e)
            } catch (e: Exception) {
                logger.e("Unexpected error in asymmetric decryption", e)
                throw CryptoOperationException("Decryption failed", e)
            }
    }

    override suspend fun listKeys(): List<String>  =
        withContext(Dispatchers.IO){
        return@withContext try {
            val keyRingName = KeyRingName.of(
                projectId,
                remoteConfig.getKeyRingLocationId(),
                remoteConfig.getKeyRingId()
            )

            kmsClient.listCryptoKeys(keyRingName)
                .iterateAll()
                .map { it.name }
                .also { logger.d("Listed ${it.size} keys") }
        } catch (e: ApiException) {
            logger.e("Failed to list keys", e)
            throw GoogleKmsException("Key listing failed: ${e.message}", e)
        }
    }

    // --- Private Helpers ---
    private suspend fun getPublicKey(): java.security.PublicKey =
        withContext(Dispatchers.IO) {
        val cacheKey = "${remoteConfig.getAsymmetricKeyId()}-public"
        val now = System.currentTimeMillis()

        publicKeyCache[cacheKey]?.let { (timestamp, key) ->
            if (now - timestamp < KEY_CACHE_TTL_MILLIS) {
                logger.d("Using cached public key")
                return@let key
            } else {
                //
            }
        }

            return@withContext try {
                val keyVersionName = getLatestAsymmetricKeyVersion()
                val publicKeyResponse = kmsClient.getPublicKey(keyVersionName)

                val pemParser = PEMParser(StringReader(publicKeyResponse.pem))
                val subjectPublicKeyInfo = pemParser.readObject() as? org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
                    ?: throw IllegalStateException("Invalid PEM format")

                val keySpec = X509EncodedKeySpec(subjectPublicKeyInfo.encoded)
                val keyFactory = KeyFactory.getInstance("RSA")
                val publicKey = keyFactory.generatePublic(keySpec)

                publicKeyCache[cacheKey] = Pair(now, publicKey)
                logger.d("Refreshed public key cache")

                publicKey
            } catch (e: Exception) {
                logger.e("Failed to retrieve public key", e)
                throw CryptoOperationException("Public key retrieval failed", e)
            }
    }

    private suspend fun getLatestAsymmetricKeyVersion(): CryptoKeyVersionName =
        withContext(Dispatchers.IO) {
            val keyName = getAsymmetricKeyName()

            return@withContext kmsClient.listCryptoKeyVersions(keyName)
                .iterateAll()
                .firstOrNull { it.state == CryptoKeyVersion.CryptoKeyVersionState.ENABLED }
                ?.name
                ?.let { CryptoKeyVersionName.parse(it) }
                ?: throw InvalidKeyVersionException("No enabled versions found for key ${remoteConfig.getAsymmetricKeyId()}")
        }

    private suspend fun getSymmetricKeyName(): CryptoKeyName =
        withContext(Dispatchers.IO){
            return@withContext CryptoKeyName.of(
                projectId,
                remoteConfig.getKeyRingLocationId(),
                remoteConfig.getKeyRingId(),
                remoteConfig.getSymmetricKeyId()
            )
        }

    private suspend fun getAsymmetricKeyName(): CryptoKeyName =
        withContext(Dispatchers.IO) {
            return@withContext CryptoKeyName.of(
                projectId,
                remoteConfig.getKeyRingLocationId(),
                remoteConfig.getKeyRingId(),
                remoteConfig.getAsymmetricKeyId()
            )
        }
}