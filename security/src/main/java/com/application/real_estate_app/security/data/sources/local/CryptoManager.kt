package com.application.real_estate_app.security.data.sources.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.application.real_estate_app.core.common.errors.Result
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val AES_KEY_ALIAS = "secure_app_key"
private const val RSA_ENCRYPTION_KEY_ALIAS = "secure_rsa_encryption_key"
private const val RSA_SIGNING_KEY_ALIAS = "secure_rsa_signing_key"
private const val AES_KEY_SIZE = 256
private const val RSA_KEY_SIZE = 4096
private const val GCM_TAG_LENGTH = 128
private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
private const val SIGNATURE_ALGORITHM = "SHA256withRSA/PSS"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val RSA_ENCRYPTED_KEY_SIZE = 512  // 4096 bits = 512 bytes

class CryptoManager @Inject constructor(
    private val logger: LoggerInterface
) : ICryptoManager {

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    init {
        initializeKeys()
    }

    // region Key Management
    private fun initializeKeys() {
        if (!keyStore.containsAlias(AES_KEY_ALIAS)) generateAesKey()
        if (!keyStore.containsAlias(RSA_ENCRYPTION_KEY_ALIAS)) generateRsaEncryptionKey()
        if (!keyStore.containsAlias(RSA_SIGNING_KEY_ALIAS)) generateRsaSigningKey()
    }

    override suspend fun rotateAesKey(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            keyStore.deleteEntry(AES_KEY_ALIAS)
            generateAesKey()
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e("AES key rotation failed", e)
            Result.Error(SecurityException("Key rotation failed", e))
        }
    }

    override suspend fun rotateRsaEncryptionKey(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            keyStore.deleteEntry(RSA_ENCRYPTION_KEY_ALIAS)
            generateRsaEncryptionKey()
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e("RSA encryption key rotation failed", e)
            Result.Error(SecurityException("Key rotation failed", e))
        }
    }
    // endregion

    // region Symmetric Encryption
    override suspend fun encrypt(bytes: ByteArray): Result<ByteArray> =
        withContext(Dispatchers.IO) {
            try {
                val cipher = Cipher.getInstance(AES_TRANSFORMATION).apply {
                    init(Cipher.ENCRYPT_MODE, getAesKey())
                }
                Result.Success(cipher.iv + cipher.doFinal(bytes))
            } catch (e: Exception) {
                logger.e("Symmetric encryption failed", e)
                Result.Error(SecurityException("Encryption failed", e))
            }
        }

    override suspend fun decrypt(bytes: ByteArray): Result<ByteArray> =
        withContext(Dispatchers.IO) {
            try {
                require(bytes.size >= 12) { "Invalid encrypted payload" }

                val iv = bytes.copyOfRange(0, 12)
                val cipher = Cipher.getInstance(AES_TRANSFORMATION).apply {
                    init(Cipher.DECRYPT_MODE, getAesKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
                }
                Result.Success(cipher.doFinal(bytes, 12, bytes.size - 12))
            } catch (e: Exception) {
                logger.e("Symmetric decryption failed", e)
                Result.Error(SecurityException("Decryption failed", e))
            }
        }
    // endregion

    // region Asymmetric Encryption (Hybrid Approach)
    override suspend fun rsaEncrypt(data: ByteArray): Result<ByteArray> =
        withContext(Dispatchers.IO) {
            try {
                // Generate one-time AES key
                val aesKey = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES).apply {
                    init(AES_KEY_SIZE)
                }.generateKey()

                // Encrypt data with AES
                val cipherAes = Cipher.getInstance(AES_TRANSFORMATION).apply {
                    init(Cipher.ENCRYPT_MODE, aesKey)
                }
                val iv = cipherAes.iv
                val encryptedData = cipherAes.doFinal(data)

                // Encrypt AES key with RSA
                val cipherRsa = Cipher.getInstance(RSA_TRANSFORMATION).apply {
                    init(Cipher.ENCRYPT_MODE, getRsaEncryptionPublicKey())
                }
                val encryptedAesKey = cipherRsa.doFinal(aesKey.encoded)

                // Package: [RSA-encrypted AES key] + [IV] + [AES-encrypted data]
                Result.Success(encryptedAesKey + iv + encryptedData)
            } catch (e: Exception) {
                logger.e("RSA encryption failed", e)
                Result.Error(SecurityException("RSA encryption failed", e))
            }
        }

    override suspend fun rsaDecrypt(data: ByteArray): Result<ByteArray> =
        withContext(Dispatchers.IO) {
            try {
                require(data.size > RSA_ENCRYPTED_KEY_SIZE + 12) { "Invalid encrypted payload" }

                // Split components
                val encryptedAesKey = data.copyOfRange(0, RSA_ENCRYPTED_KEY_SIZE)
                val iv = data.copyOfRange(RSA_ENCRYPTED_KEY_SIZE, RSA_ENCRYPTED_KEY_SIZE + 12)
                val encryptedData = data.copyOfRange(RSA_ENCRYPTED_KEY_SIZE + 12, data.size)

                // Decrypt AES key
                val cipherRsa = Cipher.getInstance(RSA_TRANSFORMATION).apply {
                    init(Cipher.DECRYPT_MODE, getRsaEncryptionPrivateKey())
                }
                val aesKeyBytes = cipherRsa.doFinal(encryptedAesKey)
                val aesKey = SecretKeySpec(aesKeyBytes, KeyProperties.KEY_ALGORITHM_AES)

                // Decrypt data
                val cipherAes = Cipher.getInstance(AES_TRANSFORMATION).apply {
                    init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
                }
                Result.Success(cipherAes.doFinal(encryptedData))
            } catch (e: Exception) {
                logger.e("RSA decryption failed", e)
                Result.Error(SecurityException("RSA decryption failed", e))
            }
        }
    // endregion

    // region Digital Signatures
    override suspend fun signData(data: ByteArray): Result<ByteArray> =
        withContext(Dispatchers.IO) {
            try {
                Signature.getInstance(SIGNATURE_ALGORITHM).apply {
                    initSign(getRsaSigningPrivateKey())
                    update(data)
                }.sign().let {
                    Result.Success(it)
                }
            } catch (e: Exception) {
                logger.e("Signature generation failed", e)
                Result.Error(SecurityException("Signing failed", e))
            }
        }

    override suspend fun verifySignature(data: ByteArray, signature: ByteArray): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                Signature.getInstance(SIGNATURE_ALGORITHM).apply {
                    initVerify(getRsaSigningPublicKey())
                    update(data)
                }.verify(signature).let {
                    Result.Success(it)
                }
            } catch (e: Exception) {
                logger.e("Signature verification failed", e)
                Result.Error(SecurityException("Verification failed", e))
            }
        }
    // endregion

    // region Key Generation
    private fun generateAesKey() {
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    AES_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(AES_KEY_SIZE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    private fun generateRsaEncryptionKey() {
        KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE).apply {
            initialize(
                KeyGenParameterSpec.Builder(
                    RSA_ENCRYPTION_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(RSA_KEY_SIZE)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .build()
            )
        }.generateKeyPair()
    }

    private fun generateRsaSigningKey() {
        KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE).apply {
            initialize(
                KeyGenParameterSpec.Builder(
                    RSA_SIGNING_KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                )
                    .setKeySize(RSA_KEY_SIZE)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                    .build()
            )
        }.generateKeyPair()
    }
    // endregion

    // region Key Retrieval
    private fun getAesKey(): SecretKey =
        (keyStore.getEntry(AES_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey

    private fun getRsaEncryptionPublicKey(): PublicKey =
        (keyStore.getEntry(RSA_ENCRYPTION_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).certificate.publicKey

    private fun getRsaEncryptionPrivateKey(): PrivateKey =
        (keyStore.getEntry(RSA_ENCRYPTION_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey

    private fun getRsaSigningPublicKey(): PublicKey =
        (keyStore.getEntry(RSA_SIGNING_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).certificate.publicKey

    private fun getRsaSigningPrivateKey(): PrivateKey =
        (keyStore.getEntry(RSA_SIGNING_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey
    // endregion

    // region Password Hashing
    override suspend fun hashWithSalt(data: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
            val iterations = 600000

            val keySpec = PBEKeySpec(data.toCharArray(), salt, iterations, 256)
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec)
                .let { secret ->
                    Result.Success(
                        "pbkdf2_sha256:$iterations:${
                            Base64.encodeToString(salt, Base64.NO_WRAP)
                        }:${Base64.encodeToString(secret.encoded, Base64.NO_WRAP)}"
                    )
                }
        } catch (e: Exception) {
            logger.e("Password hashing failed", e)
            Result.Error(SecurityException("Hashing failed", e))
        }
    }

    override suspend fun verifyHash(data: String, hash: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val parts = hash.split(":")
                require(parts.size == 4 && parts[0] == "pbkdf2_sha256") { "Invalid hash format" }

                val iterations = parts[1].toInt()
                val salt = Base64.decode(parts[2], Base64.NO_WRAP)
                val storedHash = Base64.decode(parts[3], Base64.NO_WRAP)

                val keySpec = PBEKeySpec(data.toCharArray(), salt, iterations, storedHash.size * 8)
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec)
                    .let { secret ->
                        Result.Success(secret.encoded.contentEquals(storedHash))
                    }
            } catch (e: Exception) {
                logger.e("Hash verification failed", e)
                Result.Error(SecurityException("Verification failed", e))
            }
        }
    // endregion
}