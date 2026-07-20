package com.estatia.realestate.apps.core.common.exceptions

sealed class SecurityException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {

    data class HashGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash generation failed",
            throwable
        )

    data class HashVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Hash verification failed",
            throwable
        )

    data object KeyGenerationRequired :
        SecurityException(
            "Key generation required"
        )

    data object KeyRetrievalFailed :
        SecurityException(
            "Key retrieval failed"
        )

    data object KeyGenerationFailed :
        SecurityException(
            "Key generation failed"
        )

    data object InvalidKey :
        SecurityException(
            "Invalid key"
        )

    data object InvalidCredentials :
        SecurityException(
            "Invalid credentials"
        )

    data object InvalidPassword :
        SecurityException(
            "Invalid password"
        )

    data class SignatureGenerationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature generation failed",
            throwable
        )

    data class SignatureVerificationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Signature verification failed",
            throwable
        )

    data class DecryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Decryption failed",
            throwable
        )

    data class EncryptionFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Encryption failed",
            throwable
        )

    data class KeyRotationFailed(val throwable: Throwable? = null) :
        SecurityException(
            "Key rotation failed",
            throwable
        )
}