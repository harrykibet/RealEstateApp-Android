package com.application.real_estate_app.security.domain.interfaces

import com.application.real_estate_app.core_common.errors.Result
import com.application.real_estate_app.core_interface.CryptMode

/**
 * Defines security repository operations for handling encryption, decryption, signing, verification,
 * and hashing, delegating to the appropriate data sources.
 */
interface ISecurityRepo {
    suspend fun asymmetricEncrypt(data: String, mode: CryptMode): Result<String>
    suspend fun asymmetricDecrypt(encryptedData: String, mode: CryptMode): Result<String>
    suspend fun symmetricEncrypt(data: String, mode: CryptMode): Result<String>
    suspend fun symmetricDecrypt(encryptedData: String, mode: CryptMode): Result<String>
    suspend fun signData(data: String, mode: CryptMode): Result<String>
    suspend fun verifySignature(data: String, signature: String, mode: CryptMode): Result<Boolean>
    suspend fun hashWithSalt(data: String): Result<String>
    suspend fun verifyHash(data: String, combinedHash: String): Result<Boolean>
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun clearToken(): Result<Unit>
}
