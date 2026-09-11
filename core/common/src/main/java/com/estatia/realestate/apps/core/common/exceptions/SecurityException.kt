package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class SecurityException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {

@DomainModel
    data class HashGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash generation failed",
            throwable
        )

@DomainModel
    data class HashVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash verification failed",
            throwable
        )

@DomainModel
    data object KeyGenerationRequired :
        SecurityException(
            "Key generation required"
        )

@DomainModel
    data object KeyRetrievalFailed :
        SecurityException(
            "Key retrieval failed"
        )

@DomainModel
    data object KeyGenerationFailed :
        SecurityException(
            "Key generation failed"
        )

@DomainModel
    data object InvalidKey :
        SecurityException(
            "Invalid key"
        )

@DomainModel
    data object InvalidCredentials :
        SecurityException(
            "Invalid credentials"
        )

@DomainModel
    data object InvalidPassword :
        SecurityException(
            "Invalid password"
        )

@DomainModel
    data class SignatureGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature generation failed",
            throwable
        )

@DomainModel
    data class SignatureVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature verification failed",
            throwable
        )

@DomainModel
    data class DecryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Decryption failed",
            throwable
        )

@DomainModel
    data class EncryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Encryption failed",
            throwable
        )

@DomainModel
    data class KeyRotationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Key rotation failed",
            throwable
        )

@DomainModel
    data class HmacGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "HMAC generation failed",
            throwable
        )

@DomainModel
    data class InvalidApiKey(
        val msg: String,
        val throwable: Throwable? = null
    ) :
        SecurityException(
            "Invalid API key : $msg",
            throwable
        )
}
