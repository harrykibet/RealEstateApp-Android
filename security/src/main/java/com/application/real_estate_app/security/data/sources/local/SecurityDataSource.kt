package com.application.real_estate_app.security.data.sources.local

import android.util.Base64
import com.application.real_estate_app.security.domain.interfaces.ICryptoManager
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import com.application.real_estate_app.core.common.errors.Result
import com.application.real_estate_app.core.common.errors.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SecurityDataSource(
    private val cryptoManager: ICryptoManager
) : ISecurityDataSource {

    override suspend fun encrypt(data: String): Result<String> {
        return cryptoManager.encrypt(data.toByteArray()).map { encryptedBytes ->
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }

    override suspend fun decrypt(encryptedData: String): Result<String> {
        return cryptoManager.decrypt(Base64.decode(encryptedData, Base64.DEFAULT)).map { decryptedBytes ->
            String(decryptedBytes)
        }
    }

    override suspend fun signData(data: String): Result<String> {
        return cryptoManager.signData(data.toByteArray()).map { signatureBytes ->
            Base64.encodeToString(signatureBytes, Base64.DEFAULT)
        }
    }

    override suspend fun verifySignature(data: String, signature: String): Result<Boolean> {
        return cryptoManager.verifySignature(data.toByteArray(), Base64.decode(signature, Base64.DEFAULT))
    }

    override suspend fun rsaEncrypt(data: String): Result<String> {
        return cryptoManager.rsaEncrypt(data.toByteArray()).map { encryptedBytes ->
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }
    }

    override suspend fun rsaDecrypt(encryptedData: String): Result<String> {
        return cryptoManager.rsaDecrypt(Base64.decode(encryptedData, Base64.DEFAULT)).map { decryptedBytes ->
            String(decryptedBytes)
        }
    }

    override suspend fun rsaSignData(data: String): Result<String> {
        return cryptoManager.signData(data.toByteArray()).map { signatureBytes ->
            Base64.encodeToString(signatureBytes, Base64.DEFAULT)
        }
    }

    override suspend fun hashWithSalt(data: String): Result<String> {
        return withContext(Dispatchers.IO) { cryptoManager.hashWithSalt(data) }
    }

    override suspend fun verifyHash(data: String, combinedHash: String): Result<Boolean> {
        return withContext(Dispatchers.IO) { cryptoManager.verifyHash(data, combinedHash) }
    }
}
