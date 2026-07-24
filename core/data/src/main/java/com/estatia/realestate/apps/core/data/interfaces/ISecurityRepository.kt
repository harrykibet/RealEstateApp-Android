package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

/**
 * Defines security-related operations such as encryption, decryption, signing, and verification.
 */
interface ISecurityRepository {
    suspend fun asymmetricEncrypt(data: String): AppResult<String>
    suspend fun asymmetricDecrypt(encryptedData: String): AppResult<String>
    suspend fun symmetricEncrypt(data: String): AppResult<String>
    suspend fun symmetricDecrypt(encryptedData: String): AppResult<String>
    suspend fun signData(data: String): AppResult<String>
    suspend fun verifySignature(data: String, signature: String): AppResult<Boolean>
    suspend fun hashWithSalt(data: String): AppResult<String>
    suspend fun verifyHash(data: String, combinedHash: String): AppResult<Boolean>
    suspend fun saveToken(token: String): AppResult<Unit>
    suspend fun getToken(): AppResult<String?>
    suspend fun clearToken(): AppResult<Unit>
}
