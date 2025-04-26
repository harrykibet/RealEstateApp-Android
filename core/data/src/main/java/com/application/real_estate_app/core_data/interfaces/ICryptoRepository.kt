package com.application.real_estate_app.core_data.interfaces

import com.application.real_estate_app.core_common.errors.Result

interface ICryptoRepository {

    // Local operations
    suspend fun localSymmetricEncryption(plainText: String): Result<String>
    suspend fun localSymmetricDecryption(encryptedText: String): Result<String>
    suspend fun localAsymmetricDecryption(encryptedText: String): Result<String>
    suspend fun localAsymmetricEncryption(plainText: String): Result<String>
    suspend fun localDataSigning(plainText: String): Result<String>
    suspend fun localSignatureVerification(plainText: String, signature: String): Result<Boolean>
    suspend fun localHashingWithSalt(plainText: String): Result<String>
    suspend fun localHashVerification(plainText: String, hashedText: String): Result<Boolean>

    //Remote operations
    suspend fun remoteSymmetricEncryption(plainText: String): Result<String>
    suspend fun remoteSymmetricDecryption(encryptedText: String): Result<String>
    suspend fun remoteAsymmetricEncryption(plainText: String): Result<String>
    suspend fun remoteAsymmetricDecryption(encryptedText: String): Result<String>
    suspend fun remoteDataSigning(plainText: String): Result<String>
    suspend fun remoteSignatureVerification(plainText: String, signature: String): Result<Boolean>
    suspend fun listRemoteCryptoKeys(): Result<List<String>>
}