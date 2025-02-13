package com.application.real_estate_app.security.data.sources.local

import android.util.Base64
import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import com.application.real_estate_app.core.common.errors.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecurityDataSource(
    private val cryptoManager: ICryptoManager
) : ISecurityDataSource {

    override suspend fun encrypt(data: String): Result<String> = try {
        val encryptedBytes = cryptoManager.encrypt(data.toByteArray())
        Result.Success(Base64.encodeToString(encryptedBytes, Base64.DEFAULT))
    } catch (e: Exception) {
        Result.Error(SecurityException("Encryption failed", e))
    }

    override suspend fun decrypt(encryptedData: String): Result<String> = try {
        val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cryptoManager.decrypt(decodedBytes)
        Result.Success(String(decryptedBytes))
    } catch (e: Exception) {
        Result.Error(SecurityException("Decryption failed", e))
    }

    override suspend fun signData(data: String): Result<String> = try {
        val signatureBytes = cryptoManager.signData(data.toByteArray())
        Result.Success(Base64.encodeToString(signatureBytes, Base64.DEFAULT))
        } catch (e: Exception) {
        Result.Error(SecurityException("Signing failed", e))
    }

    override suspend fun verifySignature(data: String, signature: String): Result<Boolean>
    = try {
        val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        Result.Success(cryptoManager.verifySignature(data.toByteArray(), signatureBytes))
        } catch (e: Exception) {
        Result.Error(SecurityException("Signature verification failed", e))
    }

    override suspend fun rsaEncrypt(data: String): Result<String>
    = try {
        val encryptedBytes = cryptoManager.rsaEncrypt(data.toByteArray())
        Result.Success(Base64.encodeToString(encryptedBytes, Base64.DEFAULT))
        } catch (e: Exception) {
            Result.Error(SecurityException("RSA encryption failed", e))
    }

    override suspend fun rsaDecrypt(encryptedData: String): Result<String>
    = try {
        val decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cryptoManager.rsaDecrypt(decodedBytes)
        Result.Success(String(decryptedBytes))
        } catch (e: Exception) {
            Result.Error(SecurityException("RSA decryption failed", e))
    }

    override suspend fun rsaSignData(data: String): Result<String>
    = try {
        val signatureBytes = cryptoManager.signData(data.toByteArray())
        Result.Success(Base64.encodeToString(signatureBytes, Base64.DEFAULT))
        } catch (e: Exception) {
            Result.Error(SecurityException("RSA signing failed", e))
    }

    override suspend fun hashWithSalt(data: String): Result<String> = try {
        withContext(Dispatchers.IO) {
            cryptoManager.hashWithSalt(data)
        }
    } catch (e: Exception) {
        Result.Error(SecurityException("Hashing failed", e))
    }

    override suspend fun verifyHash(data: String, combinedHash: String): Result<Boolean> = try {
        withContext(Dispatchers.IO) {
            Result.Success(cryptoManager.verifyHash(data, combinedHash))
        }
    } catch (e: Exception) {
        Result.Error(SecurityException("Hash verification failed", e))
    }
}
