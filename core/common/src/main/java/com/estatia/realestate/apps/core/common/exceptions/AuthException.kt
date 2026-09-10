package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object InvalidCredentials :
        AuthException(
            "Invalid credentials"
        )


@Helper
    data object UserAlreadyExists :
        AuthException(
            "User already exists"
        )


@Helper
    data object UserNotFound :
        AuthException(
            "User not found"
        )


@Helper
    data object InvalidEmail :
        AuthException(
            "Invalid email"
        )


@Helper
    data object MultiFactorRequired :
        AuthException(
            "Multi-factor authentication required"
        )


@Helper
    data object SignUpFailed :
        AuthException(
            "Sign up failed"
        )


@Helper
    data object SignInFailed :
        AuthException(
            "Sign in failed"
        )


@Helper
    data object EmailVerificationRequired :
        AuthException(
            "Email verification required"
        )


@Helper
    data object UserNotAuthenticated :
        AuthException(
            "User is not authenticated"
        )

@Helper
    data object TooManyRequests :
        AuthException(
            "Too many requests"
        )

@Helper
    data object InvalidPhoneNumber :
        AuthException(
            "Invalid phone number"
        )

@Helper
    data object SessionExpired :
        AuthException(
            "Session expired"
        )

@Helper
    data object OperationNotAllowed :
        AuthException(
            "Operation not allowed"
        )

@Helper
    data class Unknown(
        val original: Throwable
    ) : AuthException(
        "Unknown auth error",
        original
    )

@Helper
    data object ActionCodeInvalid :
        AuthException(
            "Action code is invalid"
        )

@Helper
    data class TokenError(val msg: String) :
        AuthException(
            "Token error: $msg"
        )
}
