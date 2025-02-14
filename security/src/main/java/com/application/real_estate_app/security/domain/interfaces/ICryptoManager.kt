package com.application.real_estate_app.security.domain.interfaces

import com.application.real_estate_app.core.common.errors.Result

interface ICryptoManager {
    suspend fun encrypt(bytes: ByteArray): Result<ByteArray>
    suspend fun decrypt(bytes: ByteArray): Result<ByteArray>
    suspend fun hashWithSalt(data: String): Result<String>
    suspend fun verifyHash(data: String, hash: String): Result<Boolean>
    suspend fun rsaDecrypt(data: ByteArray): Result<ByteArray>
    suspend fun signData(data: ByteArray): Result<ByteArray>
    suspend fun rsaEncrypt(data: ByteArray): Result<ByteArray>
    suspend fun verifySignature(data: ByteArray, signature: ByteArray): Result<Boolean>
    suspend fun rotateAesKey(): Result<Unit>
    suspend fun rotateRsaEncryptionKey(): Result<Unit>
}