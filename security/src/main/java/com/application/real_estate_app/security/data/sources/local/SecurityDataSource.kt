package com.application.real_estate_app.security.data.sources.local

import android.util.Base64
import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import com.application.real_estate_app.core.common.errors.Result
import com.application.real_estate_app.core.common.errors.map
import com.application.real_estate_app.security.domain.interfaces.IGoogleCloudKmsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * [SecurityDataSource] is an implementation of [ISecurityDataSource] that provides methods for
 * various security operations, including encryption, decryption, signing, signature verification
 * and hashing.
 *
 * It delegates cryptographic operations to [ICryptoManager] for local security
 * and [IGoogleCloudKmsManager] for cloud-based security.
 *
 * @property localCryptoManager An instance of [ICryptoManager] responsible for local cryptographic operations.
 * @property remoteCryptoManager An instance of [IGoogleCloudKmsManager] responsible for remote cryptographic operations.
 */
class SecurityDataSource(
    private val localCryptoManager: ICryptoManager,
    private val remoteCryptoManager: IGoogleCloudKmsManager
) : ISecurityDataSource {

    // Local encryption using ICryptoManager
    override suspend fun localSymmetricEncryption(plainText: String): Result<String> {
        return localCryptoManager.aesEncrypt(plainText.toByteArray()).map { encryptedBytes ->
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }

    override suspend fun localSymmetricDecryption(encryptedText: String): Result<String> {
        return localCryptoManager.aesDecrypt(Base64.decode(encryptedText, Base64.DEFAULT)).map { decryptedBytes ->
            String(decryptedBytes)
        }
    }

    // Cloud-based encryption using Google Cloud KMS
    override suspend fun remoteSymmetricEncryption(plainText: String): Result<String> {
        return try {
            Result.Success(remoteCryptoManager.encryptDataSymmetric(plainText))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    override suspend fun remoteSymmetricDecryption(encryptedText: String): Result<String> {
        return try {
            Result.Success(remoteCryptoManager.decryptDataSymmetric(encryptedText))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    // Cloud-based asymmetric encryption
    override suspend fun remoteAsymmetricEncryption(plainText: String): Result<String> {
        return try {
            Result.Success(remoteCryptoManager.encryptDataAsymmetric(plainText))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    override suspend fun remoteAsymmetricDecryption(encryptedText: String): Result<String> {
        return try {
            Result.Success(remoteCryptoManager.decryptDataAsymmetric(encryptedText))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }


    // Local  asymmetric encryption
    override suspend fun localAsymmetricEncryption(plainText: String): Result<String> {
        return localCryptoManager.rsaEncrypt(plainText.toByteArray()).map { encryptedBytes ->
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }

    override suspend fun localAsymmetricDecryption(encryptedText: String): Result<String> {
        return localCryptoManager.rsaDecrypt(Base64.decode(encryptedText, Base64.DEFAULT)).map { decryptedBytes ->
            String(decryptedBytes)
        }
    }


    // Local signature using ICryptoManager
    override suspend fun localDataSigning(plainText: String): Result<String> {
        return localCryptoManager.signData(plainText.toByteArray()).map { signatureBytes ->
            Base64.encodeToString(signatureBytes, Base64.DEFAULT)
        }
    }

    override suspend fun localSignatureVerification(plainText: String, signature: String): Result<Boolean> {
        return localCryptoManager.verifySignature(plainText.toByteArray(), Base64.decode(signature, Base64.DEFAULT))
    }

    // Cloud-based signing using Google Cloud KMS
    override suspend fun remoteDataSigning(plainText: String): Result<String> {
        return try {
            Result.Success(remoteCryptoManager.signData(plainText))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    override suspend fun remoteSignatureVerification(plainText: String, signature: String): Result<Boolean> {
        return try {
            Result.Success(remoteCryptoManager.verifySignature(plainText, signature))
        } catch (error: Exception) {
            Result.Error(error)
        }
    }

    // Local Data Hashing
    override suspend fun localHashingWithSalt(plainText: String): Result<String> {
        return withContext(Dispatchers.IO) { localCryptoManager.hashWithSalt(plainText) }
    }

    override suspend fun localHashVerification(plainText: String, hashedText: String): Result<Boolean> {
        return withContext(Dispatchers.IO) { localCryptoManager.verifyHash(plainText, hashedText) }
    }

    // Retrieve available encryption keys from Google Cloud KMS
    override suspend fun listRemoteCryptoKeys(): Result<List<String>> {
        return try {
            Result.Success(remoteCryptoManager.listKeys())
        } catch (error: Exception) {
            Result.Error(error)
        }
    }
}
