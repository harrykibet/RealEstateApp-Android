package com.application.real_estate_app.core.domain.interfaces

import com.application.real_estate_app.core.common.errors.Result

interface ISecurity {
    suspend fun encryptData(data: String): Result<String>
    suspend fun decryptData(encryptedData: String): Result<String>
    suspend fun hashWithSalt(data: String): Result<String>
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun clearToken(): Result<Unit>
    suspend fun verifyHash(data: String, combinedHash: String): Result<Boolean>
    suspend fun rsaSignData(data: String): Result<String>
    suspend fun rsaDecrypt(encryptedData: String): Result<String>
    suspend fun rsaEncrypt(data: String): Result<String>
    suspend fun verifySignature(data: String, signature: String): Result<Boolean>
}