package com.estatia.realestate.apps.core.data.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface ICryptoRepository {

    // Local operations
    suspend fun localSymmetricEncryption(plainText: String): AppResult<String>
    suspend fun localSymmetricDecryption(encryptedText: String): AppResult<String>
    suspend fun localAsymmetricDecryption(encryptedText: String): AppResult<String>
    suspend fun localAsymmetricEncryption(plainText: String): AppResult<String>
    suspend fun localDataSigning(plainText: String): AppResult<String>
    suspend fun localSignatureVerification(plainText: String, signature: String): AppResult<Boolean>
    suspend fun localHashingWithSalt(plainText: String): AppResult<String>
    suspend fun localHashVerification(plainText: String, hashedText: String): AppResult<Boolean>

    //Remote operations
    suspend fun remoteSymmetricEncryption(plainText: String): AppResult<String>
    suspend fun remoteSymmetricDecryption(encryptedText: String): AppResult<String>
    suspend fun remoteAsymmetricEncryption(plainText: String): AppResult<String>
    suspend fun remoteAsymmetricDecryption(encryptedText: String): AppResult<String>
    suspend fun remoteDataSigning(plainText: String): AppResult<String>
    suspend fun remoteSignatureVerification(plainText: String, signature: String): AppResult<Boolean>
    suspend fun listRemoteCryptoKeys(): AppResult<List<String>>
}