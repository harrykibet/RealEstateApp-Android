package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.data.CryptMode

/**
 * Defines security-related operations such as encryption, decryption, signing, and verification.
 */
interface ISecurityRepository {
    suspend fun asymmetricEncrypt(data: String, mode: CryptMode): AppResult<String>
    suspend fun asymmetricDecrypt(encryptedData: String, mode: CryptMode): AppResult<String>
    suspend fun symmetricEncrypt(data: String, mode: CryptMode): AppResult<String>
    suspend fun symmetricDecrypt(encryptedData: String, mode: CryptMode): AppResult<String>
    suspend fun signData(data: String, mode: CryptMode): AppResult<String>
    suspend fun verifySignature(data: String, signature: String, mode: CryptMode): AppResult<Boolean>
    suspend fun hashWithSalt(data: String): AppResult<String>
    suspend fun verifyHash(data: String, combinedHash: String): AppResult<Boolean>
    suspend fun saveToken(token: String): AppResult<Unit>
    suspend fun getToken(): AppResult<String?>
    suspend fun clearToken(): AppResult<Unit>
}


