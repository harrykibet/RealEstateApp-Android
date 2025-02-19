package com.application.real_estate_app.security.data.repositories

import com.application.real_estate_app.core.common.errors.Result
import com.application.real_estate_app.core.domain.interfaces.CryptMode
import com.application.real_estate_app.core.domain.interfaces.ISecurity
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import com.application.real_estate_app.security.domain.interfaces.ISecurityRepo
import com.application.real_estate_app.security.domain.interfaces.ITokenLocalDataSource

/**
 * Repository handling encryption, decryption, signing, verification, and token management.
 */
class SecurityRepository(
    private val securityDataSource: ISecurityDataSource,
    private val tokenDataSource: ITokenLocalDataSource
) : ISecurityRepo, ISecurity {

    override suspend fun asymmetricEncrypt(data: String, mode: CryptMode): Result<String> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localAsymmetricEncryption(data)
            CryptMode.REMOTE -> securityDataSource.remoteAsymmetricEncryption(data)
        }
    }

    override suspend fun asymmetricDecrypt(encryptedData: String, mode: CryptMode): Result<String> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localAsymmetricDecryption(encryptedData)
            CryptMode.REMOTE -> securityDataSource.remoteAsymmetricDecryption(encryptedData)
        }
    }

    override suspend fun symmetricEncrypt(data: String, mode: CryptMode): Result<String> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localSymmetricEncryption(data)
            CryptMode.REMOTE -> securityDataSource.remoteSymmetricEncryption(data)
        }
    }

    override suspend fun symmetricDecrypt(encryptedData: String, mode: CryptMode): Result<String> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localSymmetricDecryption(encryptedData)
            CryptMode.REMOTE -> securityDataSource.remoteSymmetricDecryption(encryptedData)
        }
    }

    override suspend fun signData(data: String, mode: CryptMode): Result<String> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localDataSigning(data)
            CryptMode.REMOTE -> securityDataSource.remoteDataSigning(data)
        }
    }

    override suspend fun verifySignature(data: String, signature: String, mode: CryptMode): Result<Boolean> {
        return when (mode) {
            CryptMode.LOCAL -> securityDataSource.localSignatureVerification(data, signature)
            CryptMode.REMOTE -> securityDataSource.remoteSignatureVerification(data, signature)
        }
    }

    override suspend fun hashWithSalt(data: String): Result<String> =
        securityDataSource.localHashingWithSalt(data)

    override suspend fun verifyHash(data: String, combinedHash: String): Result<Boolean> =
        securityDataSource.localHashVerification(data, combinedHash)

    override suspend fun saveToken(token: String): Result<Unit> =
        tokenDataSource.saveToken(token)

    override suspend fun getToken(): Result<String?> =
        tokenDataSource.getToken()

    override suspend fun clearToken(): Result<Unit> =
        tokenDataSource.clearToken()
}
