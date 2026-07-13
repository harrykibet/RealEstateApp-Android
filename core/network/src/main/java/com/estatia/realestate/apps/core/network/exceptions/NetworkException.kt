package com.estatia.realestate.apps.core.network.exceptions

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {


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
        )


    data object RateLimited :
        NetworkException(
            "Rate limited"
        )

    data class Unknown(
        val original: Throwable
    ) :
        NetworkException(
            message = "Unknown network error",
            cause = original
        )
}