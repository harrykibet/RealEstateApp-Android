package com.estatia.realestate.apps.core.network.exceptions

import org.apache.http.auth.AuthenticationException

open class SecretsManagerException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class AuthenticationFailure(cause: Throwable) :
        SecretsManagerException("Secret validation failed", cause)

    class Retryable(message: String, cause: Throwable) :
        SecretsManagerException(message, cause)

    companion object {
        fun from(e: Throwable): SecretsManagerException = when (e) {
            is AuthenticationException -> AuthenticationFailure(e)
            else -> SecretsManagerException("Secret operation failed", e)
        }
    }
}