package com.application.real_estate_app.security.domain.interfaces

import com.application.real_estate_app.core.common.errors.Result

interface ICryptoManager {
    suspend fun encrypt(bytes: ByteArray): ByteArray
    suspend fun decrypt(bytes: ByteArray): ByteArray
    suspend fun hashWithSalt(data: String): Result<String>
    suspend fun verifyHash(data: String, combinedHash: String): Boolean
    suspend fun rsaDecrypt(data: ByteArray): ByteArray
    suspend fun signData(data: ByteArray): ByteArray
    suspend fun rsaEncrypt(data: ByteArray): ByteArray
    suspend fun verifySignature(data: ByteArray, signatureBytes: ByteArray): Boolean
}