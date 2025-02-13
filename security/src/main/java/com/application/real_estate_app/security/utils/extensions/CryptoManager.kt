package com.application.real_estate_app.security.utils.extensions

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.application.real_estate_app.core.common.errors.Result
import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.*
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

private const val TAG = "CryptoManager"
private const val AES_KEY_ALIAS = "secure_app_key"
private const val RSA_KEY_ALIAS = "secure_rsa_key"
private const val KEY_SIZE = 256
private const val RSA_KEY_SIZE = 2048
private const val GCM_TAG_LENGTH = 128
private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
private const val SIGNATURE_ALGORITHM = "SHA256withRSA/PSS"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"

class CryptoManager @Inject constructor() : ICryptoManager {

    private val secretKey: SecretKey
    private val keyStore: KeyStore =
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    init {

        secretKey = if (keyStore.containsAlias(AES_KEY_ALIAS)) {
            (keyStore.getEntry(AES_KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            generateSecretKey()
        }

        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            generateRSAKeyPair()
        }
    }

    override suspend fun encrypt(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(bytes)
        iv + encryptedBytes
    }

    override suspend fun decrypt(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val iv = bytes.copyOfRange(0, 12)
        val encryptedData = bytes.copyOfRange(12, bytes.size)

        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        cipher.doFinal(encryptedData)
    }

    override suspend fun rsaEncrypt(data: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val publicKey = (keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).certificate.publicKey
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        cipher.doFinal(data)
    }

    override suspend fun rsaDecrypt(data: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val privateKey = (keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        cipher.doFinal(data)
    }

    override suspend fun signData(data: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val privateKey = (keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.setParameter(PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1))
        signature.initSign(privateKey)
        signature.update(data)
        signature.sign()
    }

    override suspend fun verifySignature(data: ByteArray, signatureBytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        val publicKey = (keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).certificate.publicKey
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.setParameter(PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1))
        signature.initVerify(publicKey)
        signature.update(data)
        signature.verify(signatureBytes)
    }

    override suspend fun hashWithSalt(data: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                val iterations = 600000
                val keyLength = 256

                val spec = PBEKeySpec(data.toCharArray(), salt, iterations, keyLength)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val hash = factory.generateSecret(spec).encoded

                val saltB64 = Base64.encodeToString(salt, Base64.NO_WRAP)
                val hashB64 = Base64.encodeToString(hash, Base64.NO_WRAP)
                val combined = "pbkdf2_sha256:$iterations:$saltB64:$hashB64"

                Result.Success(combined)
            }
        } catch (e: Exception) {
            Result.Error(SecurityException("Hashing failed", e))
        }
    }

    override suspend fun verifyHash(data: String, combinedHash: String): Boolean = withContext(Dispatchers.IO) {
        val parts = combinedHash.split(":")
        if (parts.size != 4 || parts[0] != "pbkdf2_sha256") {
            throw SecurityException("Invalid hash format")
        }
        val iterations = parts[1].toIntOrNull() ?: throw SecurityException("Invalid iterations value")
        val salt = Base64.decode(parts[2], Base64.NO_WRAP)
        val originalHash = Base64.decode(parts[3], Base64.NO_WRAP)

        val spec = PBEKeySpec(data.toCharArray(), salt, iterations, originalHash.size * 8)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val testHash = factory.generateSecret(spec).encoded

        originalHash.contentEquals(testHash)
    }

    private fun generateSecretKey(): SecretKey {
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(AES_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_SIZE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    private fun generateRSAKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(RSA_KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setKeySize(RSA_KEY_SIZE)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                .build()
        )
        keyPairGenerator.generateKeyPair()
    }
}
