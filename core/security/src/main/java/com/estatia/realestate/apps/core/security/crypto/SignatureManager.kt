package com.estatia.realestate.apps.core.security.crypto


import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.getOrThrow
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.IKeyStoreManager
import com.estatia.realestate.apps.core.security.interfaces.ISignatureManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.Signature
import javax.inject.Inject
import javax.inject.Singleton


private const val SIGNATURE_ALGORITHM =
    "SHA256withRSA/PSS"


@Singleton
class SignatureManager @Inject constructor(
    private val keyStoreManager: IKeyStoreManager,
    private val cryptoExecutor: ICryptoExecutor
) : ISignatureManager {


    override suspend fun sign(
        data: ByteArray,
        keyAlias: String
    ): AppResult<ByteArray> =
        cryptoExecutor.execute(SecurityException.SignatureGenerationFailed()) {
            withContext(Dispatchers.IO) {


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


                signer.sign()
            }
        }


    override suspend fun verify(
        data: ByteArray,
        signature: ByteArray,
        keyAlias: String
    ): AppResult<Boolean> =
        cryptoExecutor.execute(SecurityException.SignatureVerificationFailed()) {
            withContext(Dispatchers.IO) {


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



                verifier.verify(
                    signature
                )
            }
        }
}
