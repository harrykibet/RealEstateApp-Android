package com.estatia.realestate.apps.core.security.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton


private const val ANDROID_KEYSTORE =
    "AndroidKeyStore"


private const val AES_KEY_SIZE =
    256


private const val RSA_KEY_SIZE =
    2048


@Singleton
class AndroidKeyStoreManager @Inject constructor() : IKeyStoreManager {


    private val keyStore =
        KeyStore.getInstance(
            ANDROID_KEYSTORE
        ).apply {
            load(null)
        }


    override suspend fun initialize():
            AppResult<Unit> =
        withContext(Dispatchers.IO) {

            try {

                AppResult.Success(Unit)

            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.KeyGenerationFailed
                )
            }
        }


    override suspend fun generateAesKey(
        alias: String
    ): AppResult<Unit> =
        withContext(Dispatchers.IO) {

            try {

                KeyGenerator
                    .getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        ANDROID_KEYSTORE
                    )
                    .apply {

                        init(
                            KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT or
                                        KeyProperties.PURPOSE_DECRYPT
                            )
                                .setBlockModes(
                                    KeyProperties.BLOCK_MODE_GCM
                                )
                                .setEncryptionPaddings(
                                    KeyProperties.ENCRYPTION_PADDING_NONE
                                )
                                .setKeySize(
                                    AES_KEY_SIZE
                                )
                                .setRandomizedEncryptionRequired(true)
                                .build()
                        )
                    }
                    .generateKey()


                AppResult.Success(Unit)


            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.KeyGenerationFailed
                )
            }
        }


    override suspend fun generateRsaEncryptionKey(
        alias: String
    ): AppResult<Unit> =
        generateRsaKey(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )


    override suspend fun generateRsaSigningKey(
        alias: String
    ): AppResult<Unit> =
        generateRsaKey(
            alias,
            KeyProperties.PURPOSE_SIGN or
                    KeyProperties.PURPOSE_VERIFY
        )


    private suspend fun generateRsaKey(
        alias: String,
        purposes: Int
    ): AppResult<Unit> =
        withContext(Dispatchers.IO) {

            try {


                KeyPairGenerator
                    .getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA,
                        ANDROID_KEYSTORE
                    )
                    .apply {

                        initialize(
                            KeyGenParameterSpec.Builder(
                                alias,
                                purposes
                            )
                                .setKeySize(
                                    RSA_KEY_SIZE
                                )
                                .setEncryptionPaddings(
                                    KeyProperties.ENCRYPTION_PADDING_RSA_OAEP
                                )
                                .setSignaturePaddings(
                                    KeyProperties.SIGNATURE_PADDING_RSA_PSS
                                )
                                .build()
                        )
                    }
                    .generateKeyPair()



                AppResult.Success(Unit)


            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.KeyGenerationFailed
                )
            }
        }


    override suspend fun rotateKey(
        alias: String
    ): AppResult<Unit> =
        withContext(Dispatchers.IO) {

            try {

                if (keyStore.containsAlias(alias)) {
                    keyStore.deleteEntry(alias)
                }


                AppResult.Success(Unit)


            } catch (e: Exception) {

                AppResult.Error(
                    SecurityException.KeyRotationFailed(e)
                )
            }
        }

    override fun getKeyPair(
        alias:String
    ):AppResult<KeyPair>{


        return try {


            val entry =
                keyStore.getEntry(
                    alias,
                    null
                ) as KeyStore.PrivateKeyEntry


            AppResult.Success(

                KeyPair(
                    entry.certificate.publicKey,
                    entry.privateKey
                )
            )


        }catch(e:Exception){

            AppResult.Error(
                SecurityException.KeyRetrievalFailed
            )
        }
    }



    override fun getSecretKey(
        alias: String
    ): SecretKey? {


        return (
                keyStore.getEntry(
                    alias,
                    null
                ) as? KeyStore.SecretKeyEntry
                )?.secretKey

    }


    override fun getPrivateKey(
        alias: String
    ): PrivateKey? {


        return (
                keyStore.getEntry(
                    alias,
                    null
                ) as? KeyStore.PrivateKeyEntry
                )?.privateKey

    }


    override fun getPublicKey(
        alias: String
    ): PublicKey? {


        return (
                keyStore.getEntry(
                    alias,
                    null
                ) as? KeyStore.PrivateKeyEntry
                )
            ?.certificate
            ?.publicKey

    }

    override fun deleteKey(
        alias:String
    ):AppResult<Unit>{

        return try {

            keyStore.deleteEntry(alias)

            AppResult.Success(Unit)

        }catch(e:Exception){

            AppResult.Error(
                SecurityException.KeyRotationFailed(e)
            )
        }
    }


    override fun containsKey(
        alias: String
    ): Boolean =
        keyStore.containsAlias(alias)
}