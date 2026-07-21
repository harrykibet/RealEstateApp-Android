package com.estatia.realestate.apps.core.security.crypto


import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.Signature
import javax.inject.Inject


private const val SIGNATURE_ALGORITHM =
    "SHA256withRSA/PSS"



class SignatureManager @Inject constructor(
    private val keyStoreManager: IKeyStoreManager
) : ISignatureManager {



    override suspend fun sign(
        data: ByteArray,
        keyAlias: String
    ): AppResult<ByteArray> =
        withContext(Dispatchers.IO) {


            try {


                val keyPair =
                    keyStoreManager
                        .getKeyPair(keyAlias)
                        .getOrThrow()



                val signer =
                    Signature.getInstance(
                        SIGNATURE_ALGORITHM
                    )


                signer.initSign(
                    keyPair.private
                )


                signer.update(data)


                AppResult.Success(
                    signer.sign()
                )


            } catch(e:Exception) {


                AppResult.Error(
                    SecurityException.SignatureGenerationFailed(e)
                )
            }
        }





    override suspend fun verify(
        data: ByteArray,
        signature: ByteArray,
        keyAlias: String
    ): AppResult<Boolean> =
        withContext(Dispatchers.IO) {


            try {


                val keyPair =
                    keyStoreManager
                        .getKeyPair(keyAlias)
                        .getOrThrow()



                val verifier =
                    Signature.getInstance(
                        SIGNATURE_ALGORITHM
                    )


                verifier.initVerify(
                    keyPair.public
                )


                verifier.update(data)



                AppResult.Success(
                    verifier.verify(
                        signature
                    )
                )


            } catch(e:Exception) {


                AppResult.Error(
                    SecurityException.SignatureVerificationFailed(e)
                )
            }
        }
}