package com.application.real_estate_app.security.data.repositories

import com.application.real_estate_app.core.domain.interfaces.ISecurity
import com.application.real_estate_app.security.domain.interfaces.ISecurityDataSource
import com.application.real_estate_app.security.domain.interfaces.ISecurityRepo
import com.application.real_estate_app.security.domain.interfaces.ITokenLocalDataSource

class SecurityRepository(
    private val securityDataSource: ISecurityDataSource,
    private val tokenDataSource: ITokenLocalDataSource
) : ISecurityRepo, ISecurity {

    override suspend fun encryptData(data: String) = securityDataSource.encrypt(data)
    override suspend fun decryptData(encryptedData: String) = securityDataSource.decrypt(encryptedData)
    override suspend fun hashWithSalt(data: String) = securityDataSource.hashWithSalt(data)
    override suspend fun verifyHash(data: String, combinedHash: String) = securityDataSource.verifyHash(data, combinedHash)
    override suspend fun rsaSignData(data: String) = securityDataSource.rsaSignData(data)
    override suspend fun rsaDecrypt(encryptedData: String) = securityDataSource.rsaDecrypt(encryptedData)
    override suspend fun rsaEncrypt(data: String) = securityDataSource.rsaEncrypt(data)
    override suspend fun verifySignature(data: String, signature: String) = securityDataSource.verifySignature(data, signature)

    override suspend fun saveToken(token: String) = tokenDataSource.saveToken(token)
    override suspend fun getToken() = tokenDataSource.getToken()
    override suspend fun clearToken() = tokenDataSource.clearToken()
}