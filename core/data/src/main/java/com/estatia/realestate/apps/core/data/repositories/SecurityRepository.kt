package com.estatia.realestate.apps.core.data.repositories

import android.util.Base64
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.data.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.IHashManager
import com.estatia.realestate.apps.core.security.interfaces.IRsaCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import com.estatia.realestate.apps.core.security.models.HybridEncryptedPayload
import com.google.gson.Gson
import javax.inject.Inject


private const val DEFAULT_SIGNING_ALIAS = "estatia_default_signing_key"

/**
 * [SecurityRepository] acts as an orchestrator for all security-related operations.
 * It bridges high-level feature requests (using Strings) with low-level cryptographic
 * engines (using ByteArrays and structured payloads).
 */
class SecurityRepository @Inject constructor(
    private val aesGcmCryptoEngine: IAesGcmCryptoEngine,
    private val rsaCryptoEngine: IRsaCryptoEngine,
    private val signatureManager: ISignatureManager,
    private val hashManager: IHashManager,
    private val tokenDataSource: ITokenLocalDataSource,
    private val gson: Gson,
) : ISecurityRepository {

    override suspend fun asymmetricEncrypt(data: String): AppResult<String> =
        rsaCryptoEngine.encrypt(data.toByteArray()).map { gson.toJson(it) }

    override suspend fun asymmetricDecrypt(encryptedData: String): AppResult<String> {
        val payload = gson.fromJson(encryptedData, HybridEncryptedPayload::class.java)
        return rsaCryptoEngine.decrypt(payload).map { String(it) }
    }

    override suspend fun symmetricEncrypt(data: String): AppResult<String> =
        aesGcmCryptoEngine.encrypt(data.toByteArray()).map { gson.toJson(it) }

    override suspend fun symmetricDecrypt(encryptedData: String): AppResult<String> {
        val payload = gson.fromJson(encryptedData, EncryptedPayload::class.java)
        return aesGcmCryptoEngine.decrypt(payload).map { String(it) }
    }

    override suspend fun signData(data: String): AppResult<String> =
        signatureManager.sign(data.toByteArray(), DEFAULT_SIGNING_ALIAS)
            .map { Base64.encodeToString(it, Base64.DEFAULT) }

    override suspend fun verifySignature(data: String, signature: String): AppResult<Boolean> {
        val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
        return signatureManager.verify(data.toByteArray(), signatureBytes, DEFAULT_SIGNING_ALIAS)
    }

    override suspend fun hashWithSalt(data: String): AppResult<String> =
        hashManager.hashWithSalt(data)

    override suspend fun hashWithSalt(data: ByteArray, salt: ByteArray): AppResult<ByteArray> =
        hashManager.hashWithSalt(data, salt)


    override suspend fun verifyHash(data: String, combinedHash: String): AppResult<Boolean> =
        hashWithSalt(data).map { it == combinedHash }

    override suspend fun saveToken(token: String): AppResult<Unit> =
        tokenDataSource.saveToken(token)

    override suspend fun getToken(): AppResult<String?> =
        tokenDataSource.getToken()

    override suspend fun clearToken(): AppResult<Unit> =
        tokenDataSource.clearToken()
}
