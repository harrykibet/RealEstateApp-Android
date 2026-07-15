package com.estatia.realestate.apps.core.domain.exceptions

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {


    // Network Connection errors
    data object NoInternet :
        NetworkException(
            "No internet connection"
        )


    data object Timeout :
        NetworkException(
            "Request timeout"
        )


    data object ConnectionFailed :
        NetworkException(
            "Connection failed"
        )


    // HTTP errors

    data class ServerError(
        val code: Int
    ) :
        NetworkException(
            "Server error $code"
        )


    data class ClientError(
        val code: Int
    ) :
        NetworkException(
            "Client error $code"
        )


    data object Unauthorized :
        NetworkException(
            "Unauthorized"
        ) {
        private fun readResolve(): Any = Unauthorized
    }

    data object UserCreationFailed :
        NetworkException(
            "User creation failed"
        )

    data object UserNotAuthenticated:
        NetworkException(
            "User is not authenticated"
        )

    data object AuthenticationFailed:
        NetworkException(
            "Authentication failed"
        )

    data class InvalidState(val msg: String):
        NetworkException(
            "Invalid state" + (if (msg.isNotEmpty()) ": $msg" else "")
        )


    data object RateLimited :
        NetworkException(
            "Rate limited"
        )

    //Firebase errors

    data object InvalidCredentials:
        NetworkException(
            "Invalid credentials"
        )


    data object UserAlreadyExists:
        NetworkException(
            "User already exists"
        )

    data object ActionCodeInvalid:
        NetworkException(
            "Action code invalid"
        )

    data object InvalidEmail:
        NetworkException(
            "Invalid email"
        )

    data object MultiFactorRequired:
        NetworkException(
            "Multi-factor authentication required"
        )



    data object UserNotFound:
        NetworkException(
            "User not found"
        )

    data object SignUpFailed:
        NetworkException(
            "Sign up failed"
        )

    data object SignInFailed:
        NetworkException(
            "Sign in failed"
        )


    data object TooManyRequests:
        NetworkException(
            "Too many requests"
        )

    // Unknown errors
    data class Unknown(
        val original: Throwable
    ) :
        NetworkException(
            message = "Unknown network error",
            cause = original
        )
}