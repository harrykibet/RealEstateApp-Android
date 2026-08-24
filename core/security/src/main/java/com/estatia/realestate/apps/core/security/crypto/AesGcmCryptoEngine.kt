package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton


private const val AES_KEY_ALIAS = "secure_app_key_v1"

private const val AES_TRANSFORMATION =
    "AES/GCM/NoPadding"

private const val GCM_TAG_LENGTH =
    128


/**
 * Engine for performing symmetric encryption using AES-GCM.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: High-integrity symmetric encryption/decryption using the Android KeyStore.
 * - Security: Uses AES/GCM/NoPadding with a 128-bit tag and unique IVs for every operation.
 * - Concurrency: Thread-safe via [cryptoExecutor].
 * - Resilience: Delegates error handling and mapping to [cryptoExecutor].
 * - Invariants:
 *   1. IV must be precisely 12 bytes for GCM.
 *   2. The underlying [AES_KEY_ALIAS] must exist in the hardware KeyStore.
 */
@Singleton
class AesGcmCryptoEngine @Inject constructor(
    private val keyStoreManager: IKeyStoreManager,
    private val cryptoExecutor: ICryptoExecutor
) : IAesGcmCryptoEngine {



    override suspend fun encrypt(
        data: ByteArray
    ): AppResult<EncryptedPayload> =
        cryptoExecutor.execute(SecurityException.EncryptionFailed()) {
            withContext(Dispatchers.IO) {

                val key =
                    keyStoreManager
                        .getSecretKey(AES_KEY_ALIAS)
                        ?: throw SecurityException.KeyRetrievalFailed


                val cipher =
                    Cipher.getInstance(
                        AES_TRANSFORMATION
                    )


                cipher.init(
                    Cipher.ENCRYPT_MODE,
                    key
                )


                val encrypted =
                    cipher.doFinal(data)


                EncryptedPayload(
                    version = 1,
                    iv = cipher.iv,
                    ciphertext = encrypted
                )
            }
        }



    override suspend fun decrypt(
        payload: EncryptedPayload
    ): AppResult<ByteArray> =
        cryptoExecutor.execute(SecurityException.DecryptionFailed()) {
            withContext(Dispatchers.IO) {

                require(
                    payload.iv.size == 12
                )


                val key =
                    keyStoreManager
                        .getSecretKey(AES_KEY_ALIAS)
                        ?: throw SecurityException.KeyRetrievalFailed


                val cipher =
                    Cipher.getInstance(
                        AES_TRANSFORMATION
                    )


                cipher.init(
                    Cipher.DECRYPT_MODE,
                    key,
                    GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        payload.iv
                    )
                )


                cipher.doFinal(
                    payload.ciphertext
                )
            }
        }
}
