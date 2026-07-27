package com.estatia.realestate.apps.core.security.mappers

import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.security.interfaces.ISecurityExceptionTranslator
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.security.UnrecoverableKeyException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityExceptionTranslator @Inject constructor() : ISecurityExceptionTranslator {

    override fun translate(throwable: Throwable, default: SecurityException): SecurityException {
        return when (throwable) {
            is KeyStoreException -> SecurityException.KeyRetrievalFailed
            is UnrecoverableKeyException -> SecurityException.KeyRetrievalFailed
            is NoSuchAlgorithmException -> SecurityException.KeyGenerationFailed
            is NoSuchPaddingException -> SecurityException.KeyGenerationFailed
            is InvalidKeyException -> SecurityException.InvalidKey
            is SignatureException -> SecurityException.SignatureGenerationFailed(throwable)
            is IllegalBlockSizeException -> SecurityException.EncryptionFailed(throwable)
            is BadPaddingException -> SecurityException.DecryptionFailed(throwable)
            is SecurityException -> throwable
            else -> default
        }
    }
}
