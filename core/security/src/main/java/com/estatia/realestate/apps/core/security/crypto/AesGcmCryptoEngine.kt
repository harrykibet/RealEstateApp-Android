package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.IAesGcmCryptoEngine
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton


private const val AES_KEY_ALIAS = "secure_app_key_v1"

private const val AES_TRANSFORMATION =
    "AES/GCM/NoPadding"

private const val GCM_TAG_LENGTH =
    128


@Singleton
class AesGcmCryptoEngine @Inject constructor(
    private val keyStoreManager: IKeyStoreManager
) : IAesGcmCryptoEngine {



    override suspend fun encrypt(
        data: ByteArray
    ): AppResult<EncryptedPayload> =
        withContext(Dispatchers.IO) {

            try {

                val key =
                    keyStoreManager
                        .getSecretKey(AES_KEY_ALIAS)
                        ?: return@withContext AppResult.Error(
                            SecurityException.KeyRetrievalFailed
                        )


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


                AppResult.Success(
                    EncryptedPayload(
                        version = 1,
                        iv = cipher.iv,
                        ciphertext = encrypted
                    )
                )


            } catch (e: GeneralSecurityException) {

                AppResult.Error(
                    SecurityException.EncryptionFailed(e)
                )

            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.EncryptionFailed(e)
                )
            }
        }



    override suspend fun decrypt(
        payload: EncryptedPayload
    ): AppResult<ByteArray> =
        withContext(Dispatchers.IO) {

            try {


                require(
                    payload.iv.size == 12
                )


                val key =
                    keyStoreManager
                        .getSecretKey(AES_KEY_ALIAS)
                        ?: return@withContext AppResult.Error(
                            SecurityException.KeyRetrievalFailed
                        )



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


                AppResult.Success(
                    cipher.doFinal(
                        payload.ciphertext
                    )
                )


            } catch (e: GeneralSecurityException) {

                AppResult.Error(
                    SecurityException.DecryptionFailed(e)
                )


            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.DecryptionFailed(e)
                )
            }
        }
}