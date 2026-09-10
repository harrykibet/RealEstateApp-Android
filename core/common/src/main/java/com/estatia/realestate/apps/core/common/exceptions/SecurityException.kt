package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class SecurityException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {

@Helper
    data class HashGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash generation failed",
            throwable
        )

@Helper
    data class HashVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash verification failed",
            throwable
        )

@Helper
    data object KeyGenerationRequired :
        SecurityException(
            "Key generation required"
        )

@Helper
    data object KeyRetrievalFailed :
        SecurityException(
            "Key retrieval failed"
        )

@Helper
    data object KeyGenerationFailed :
        SecurityException(
            "Key generation failed"
        )

@Helper
    data object InvalidKey :
        SecurityException(
            "Invalid key"
        )

@Helper
    data object InvalidCredentials :
        SecurityException(
            "Invalid credentials"
        )

@Helper
    data object InvalidPassword :
        SecurityException(
            "Invalid password"
        )

@Helper
    data class SignatureGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature generation failed",
            throwable
        )

@Helper
    data class SignatureVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature verification failed",
            throwable
        )

@Helper
    data class DecryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Decryption failed",
            throwable
        )

@Helper
    data class EncryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Encryption failed",
            throwable
        )

@Helper
    data class KeyRotationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Key rotation failed",
            throwable
        )

@Helper
    data class HmacGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "HMAC generation failed",
            throwable
        )

@Helper
    data class InvalidApiKey(
        val msg: String,
        val throwable: Throwable? = null
    ) :
        SecurityException(
            "Invalid API key : $msg",
            throwable
        )
}
