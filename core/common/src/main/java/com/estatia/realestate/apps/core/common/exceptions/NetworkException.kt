package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object NoInternet :
        NetworkException(
            "No internet connection"
        ), RetryableException


@DomainModel
    data object Timeout :
        NetworkException(
            "Request timeout"
        ), RetryableException


@DomainModel
    data object ConnectionFailed :
        NetworkException(
            "Connection failed"
        ), RetryableException


    // HTTP/API failures

@DomainModel
    data class ServerError(
        val code: Int
    ) : NetworkException(
        "Server error :  $code"
    ), RetryableException


@DomainModel
    data class ClientError(
        val code: Int
    ) : NetworkException(
        "Client error : $code"
    )


@DomainModel
    data object Unauthorized :
        NetworkException(
            "Unauthorized"
        )


@DomainModel
    data object RateLimited :
        NetworkException(
            "Rate limited"
        )


@DomainModel
    data class Unknown(
        val original: Throwable
    ) : NetworkException(
        "Unknown network error",
        original
    )
}
