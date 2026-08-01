package com.estatia.realestate.apps.core.common.exceptions

sealed class AuthException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object InvalidCredentials :
        AuthException(
            "Invalid credentials"
        )


    data object UserAlreadyExists :
        AuthException(
            "User already exists"
        )


    data object UserNotFound :
        AuthException(
            "User not found"
        )


    data object InvalidEmail :
        AuthException(
            "Invalid email"
        )


    data object MultiFactorRequired :
        AuthException(
            "Multi-factor authentication required"
        )


    data object SignUpFailed :
        AuthException(
            "Sign up failed"
        )


    data object SignInFailed :
        AuthException(
            "Sign in failed"
        )


    data object EmailVerificationRequired :
        AuthException(
            "Email verification required"
        )


    data object UserNotAuthenticated :
        AuthException(
            "User is not authenticated"
        )

    data object TooManyRequests :
        AuthException(
            "Too many requests"
        )

    data object InvalidPhoneNumber :
        AuthException(
            "Invalid phone number"
        )

    data object SessionExpired :
        AuthException(
            "Session expired"
        )

    data object OperationNotAllowed :
        AuthException(
            "Operation not allowed"
        )

    data class Unknown(
        val original: Throwable
    ) : AuthException(
        "Unknown auth error",
        original
    )

    data object ActionCodeInvalid :
        AuthException(
            "Action code is invalid"
        )

    data class TokenError(val msg: String) :
        AuthException(
            "Token error: $msg"
        )
}
