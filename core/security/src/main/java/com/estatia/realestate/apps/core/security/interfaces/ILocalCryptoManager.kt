package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.errors.AppResult

interface ILocalCryptoManager {
    suspend fun aesEncrypt(bytes: ByteArray): AppResult<ByteArray>
    suspend fun aesDecrypt(bytes: ByteArray): AppResult<ByteArray>
    suspend fun hashWithSalt(data: String): AppResult<String>
    suspend fun verifyHash(data: String, hash: String): AppResult<Boolean>
    suspend fun rsaDecrypt(data: ByteArray): AppResult<ByteArray>
    suspend fun signData(data: ByteArray): AppResult<ByteArray>
    suspend fun rsaEncrypt(data: ByteArray): AppResult<ByteArray>
    suspend fun verifySignature(data: ByteArray, signature: ByteArray): AppResult<Boolean>
    suspend fun rotateAesKey(): AppResult<Unit>
    suspend fun rotateRsaEncryptionKey(): AppResult<Unit>
}