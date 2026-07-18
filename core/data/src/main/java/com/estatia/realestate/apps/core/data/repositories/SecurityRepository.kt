package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.data.CryptMode
import com.estatia.realestate.apps.core.data.interfaces.ICryptoRepository
import com.estatia.realestate.apps.core.data.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource

/**
 * Repository handling encryption, decryption, signing, verification, and token management.
 */
class SecurityRepository(
    private val cryptoRepository: ICryptoRepository,
    private val tokenDataSource: ITokenLocalDataSource
) : ISecurityRepository {

    override suspend fun asymmetricEncrypt(data: String, mode: CryptMode): AppResult<String> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localAsymmetricEncryption(data)
            CryptMode.REMOTE -> cryptoRepository.remoteAsymmetricEncryption(data)
        }
    }

    override suspend fun asymmetricDecrypt(encryptedData: String, mode: CryptMode): AppResult<String> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localAsymmetricDecryption(encryptedData)
            CryptMode.REMOTE -> cryptoRepository.remoteAsymmetricDecryption(encryptedData)
        }
    }

    override suspend fun symmetricEncrypt(data: String, mode: CryptMode): AppResult<String> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localSymmetricEncryption(data)
            CryptMode.REMOTE -> cryptoRepository.remoteSymmetricEncryption(data)
        }
    }

    override suspend fun symmetricDecrypt(encryptedData: String, mode: CryptMode): AppResult<String> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localSymmetricDecryption(encryptedData)
            CryptMode.REMOTE -> cryptoRepository.remoteSymmetricDecryption(encryptedData)
        }
    }

    override suspend fun signData(data: String, mode: CryptMode): AppResult<String> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localDataSigning(data)
            CryptMode.REMOTE -> cryptoRepository.remoteDataSigning(data)
        }
    }

    override suspend fun verifySignature(data: String, signature: String, mode: CryptMode): AppResult<Boolean> {
        return when (mode) {
            CryptMode.LOCAL -> cryptoRepository.localSignatureVerification(data, signature)
            CryptMode.REMOTE -> cryptoRepository.remoteSignatureVerification(data, signature)
        }
    }

    override suspend fun hashWithSalt(data: String): AppResult<String> =
        cryptoRepository.localHashingWithSalt(data)

    override suspend fun verifyHash(data: String, combinedHash: String): AppResult<Boolean> =
        cryptoRepository.localHashVerification(data, combinedHash)

    override suspend fun saveToken(token: String): AppResult<Unit> =
        tokenDataSource.saveToken(token)

    override suspend fun getToken(): AppResult<String?> =
        tokenDataSource.getToken()

    override suspend fun clearToken(): AppResult<Unit> =
        tokenDataSource.clearToken()
}