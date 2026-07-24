package com.estatia.realestate.apps.core.data.repositories

import android.util.Base64
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.data.interfaces.ISecurityRepository
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.IRsaCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import com.estatia.realestate.apps.core.security.interfaces.ITokenLocalDataSource
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import com.estatia.realestate.apps.core.security.models.HybridEncryptedPayload
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
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
    private val tokenDataSource: ITokenLocalDataSource,
    private val gson: Gson,
) : ISecurityRepository {

    override suspend fun asymmetricEncrypt(data: String): AppResult<String> {
        return rsaCryptoEngine.encrypt(data.toByteArray()).map { payload ->
            gson.toJson(payload)
        }
    }

    override suspend fun asymmetricDecrypt(encryptedData: String): AppResult<String> {
        return try {
            val payload = gson.fromJson(encryptedData, HybridEncryptedPayload::class.java)
            rsaCryptoEngine.decrypt(payload).map { String(it) }
        } catch (e: Exception) {
            AppResult.Error(SecurityException.DecryptionFailed(e))
        }
    }

    override suspend fun symmetricEncrypt(data: String): AppResult<String> {
        return aesGcmCryptoEngine.encrypt(data.toByteArray()).map { payload ->
            gson.toJson(payload)
        }
    }

    override suspend fun symmetricDecrypt(encryptedData: String): AppResult<String> {
        return try {
            val payload = gson.fromJson(encryptedData, EncryptedPayload::class.java)
            aesGcmCryptoEngine.decrypt(payload).map { String(it) }
        } catch (e: Exception) {
            AppResult.Error(SecurityException.DecryptionFailed(e))
        }
    }

    override suspend fun signData(data: String): AppResult<String> {
        return signatureManager.sign(data.toByteArray(), DEFAULT_SIGNING_ALIAS).map {
            Base64.encodeToString(it, Base64.DEFAULT)
        }
    }

    override suspend fun verifySignature(data: String, signature: String): AppResult<Boolean> {
        return try {
            val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
            signatureManager.verify(data.toByteArray(), signatureBytes, DEFAULT_SIGNING_ALIAS)
        } catch (e: Exception) {
            AppResult.Error(SecurityException.SignatureVerificationFailed(e))
        }
    }

    override suspend fun hashWithSalt(data: String): AppResult<String> = withContext(Dispatchers.Default) {
        try {
            // NOTE: In a production app, use a unique salt per item and store it.
            // This is a simplified version using a fixed static salt for consistency with previous stubs.
            val staticSalt = "ESTATIA_INTERNAL_SALT"
            val input = data + staticSalt
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray())
            AppResult.Success(Base64.encodeToString(hashBytes, Base64.DEFAULT))
        } catch (e: Exception) {
            AppResult.Error(SecurityException.HashGenerationFailed(e))
        }
    }

    override suspend fun verifyHash(data: String, combinedHash: String): AppResult<Boolean> {
        return hashWithSalt(data).map { generatedHash ->
            generatedHash == combinedHash
        }
    }

    override suspend fun saveToken(token: String): AppResult<Unit> =
        tokenDataSource.saveToken(token)

    override suspend fun getToken(): AppResult<String?> =
        tokenDataSource.getToken()

    override suspend fun clearToken(): AppResult<Unit> =
        tokenDataSource.clearToken()
}
