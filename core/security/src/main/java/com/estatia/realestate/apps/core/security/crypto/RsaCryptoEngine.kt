package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.interfaces.IRsaCryptoEngine
import com.estatia.realestate.apps.core.security.models.HybridEncryptedPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton


private const val RSA_ENCRYPTION_ALIAS =
    "secure_rsa_encryption_key_v1"


private const val RSA_TRANSFORMATION =
    "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"


private const val AES_TRANSFORMATION =
    "AES/GCM/NoPadding"


private const val AES_KEY_SIZE =
    256


private const val GCM_TAG_LENGTH =
    128


@Singleton
class RsaCryptoEngine @Inject constructor(
    private val keyStoreManager: IKeyStoreManager
) : IRsaCryptoEngine {



    override suspend fun encrypt(
        data: ByteArray
    ): AppResult<HybridEncryptedPayload> =
        withContext(Dispatchers.IO) {

            try {


                val publicKey =
                    keyStoreManager.getPublicKey(
                        RSA_ENCRYPTION_ALIAS
                    )
                        ?: return@withContext AppResult.Error(
                            SecurityException.KeyRetrievalFailed
                        )


                /*
                 * Generate one-time AES key
                 */
                val aesKey =
                    generateAesKey()



                /*
                 * Encrypt payload using AES-GCM
                 */
                val aesCipher =
                    Cipher.getInstance(
                        AES_TRANSFORMATION
                    )


                aesCipher.init(
                    Cipher.ENCRYPT_MODE,
                    aesKey
                )


                val encryptedData =
                    aesCipher.doFinal(data)



                /*
                 * Encrypt AES key using RSA
                 */
                val rsaCipher =
                    Cipher.getInstance(
                        RSA_TRANSFORMATION
                    )


                rsaCipher.init(
                    Cipher.ENCRYPT_MODE,
                    publicKey
                )


                val encryptedKey =
                    rsaCipher.doFinal(
                        aesKey.encoded
                    )



                AppResult.Success(
                    HybridEncryptedPayload(
                        version = 1,
                        encryptedKey = encryptedKey,
                        iv = aesCipher.iv,
                        ciphertext = encryptedData
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
        payload: HybridEncryptedPayload
    ): AppResult<ByteArray> =
        withContext(Dispatchers.IO) {


            try {


                val privateKey =
                    keyStoreManager.getPrivateKey(
                        RSA_ENCRYPTION_ALIAS
                    )
                        ?: return@withContext AppResult.Error(
                            SecurityException.KeyRetrievalFailed
                        )



                /*
                 * Recover AES key
                 */
                val rsaCipher =
                    Cipher.getInstance(
                        RSA_TRANSFORMATION
                    )


                rsaCipher.init(
                    Cipher.DECRYPT_MODE,
                    privateKey
                )


                val aesKeyBytes =
                    rsaCipher.doFinal(
                        payload.encryptedKey
                    )


                val aesKey =
                    SecretKeySpec(
                        aesKeyBytes,
                        "AES"
                    )



                /*
                 * Decrypt payload
                 */
                val aesCipher =
                    Cipher.getInstance(
                        AES_TRANSFORMATION
                    )


                aesCipher.init(
                    Cipher.DECRYPT_MODE,
                    aesKey,
                    GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        payload.iv
                    )
                )



                AppResult.Success(
                    aesCipher.doFinal(
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



    private fun generateAesKey(): SecretKey {

        return KeyGenerator
            .getInstance("AES")
            .apply {
                init(AES_KEY_SIZE)
            }
            .generateKey()
    }
}