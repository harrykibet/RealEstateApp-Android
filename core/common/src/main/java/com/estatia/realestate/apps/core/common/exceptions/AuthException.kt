package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object InvalidCredentials :
        AuthException(
            "Invalid credentials"
        )


@DomainModel
    data object UserAlreadyExists :
        AuthException(
            "User already exists"
        )


@DomainModel
    data object UserNotFound :
        AuthException(
            "User not found"
        )


@DomainModel
    data object InvalidEmail :
        AuthException(
            "Invalid email"
        )


@DomainModel
    data object MultiFactorRequired :
        AuthException(
            "Multi-factor authentication required"
        )


@DomainModel
    data object SignUpFailed :
        AuthException(
            "Sign up failed"
        )


@DomainModel
    data object SignInFailed :
        AuthException(
            "Sign in failed"
        )


@DomainModel
    data object EmailVerificationRequired :
        AuthException(
            "Email verification required"
        )


@DomainModel
    data object UserNotAuthenticated :
        AuthException(
            "User is not authenticated"
        )

@DomainModel
    data object TooManyRequests :
        AuthException(
            "Too many requests"
        )

@DomainModel
    data object InvalidPhoneNumber :
        AuthException(
            "Invalid phone number"
        )

@DomainModel
    data object SessionExpired :
        AuthException(
            "Session expired"
        )

@DomainModel
    data object OperationNotAllowed :
        AuthException(
            "Operation not allowed"
        )

@DomainModel
    data class Unknown(
        val original: Throwable
    ) : AuthException(
        "Unknown auth error",
        original
    )

@DomainModel
    data object ActionCodeInvalid :
        AuthException(
            "Action code is invalid"
        )

@DomainModel
    data class TokenError(val msg: String) :
        AuthException(
            "Token error: $msg"
        )
}
