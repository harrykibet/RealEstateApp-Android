package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object NoInternet :
        NetworkException(
            "No internet connection"
        ), RetryableException


@Helper
    data object Timeout :
        NetworkException(
            "Request timeout"
        ), RetryableException


@Helper
    data object ConnectionFailed :
        NetworkException(
            "Connection failed"
        ), RetryableException


    // HTTP/API failures

@Helper
    data class ServerError(
        val code: Int
    ) : NetworkException(
        "Server error :  $code"
    ), RetryableException


@Helper
    data class ClientError(
        val code: Int
    ) : NetworkException(
        "Client error : $code"
    )


@Helper
    data object Unauthorized :
        NetworkException(
            "Unauthorized"
        )


@Helper
    data object RateLimited :
        NetworkException(
            "Rate limited"
        )


@Helper
    data class Unknown(
        val original: Throwable
    ) : NetworkException(
        "Unknown network error",
        original
    )
}
