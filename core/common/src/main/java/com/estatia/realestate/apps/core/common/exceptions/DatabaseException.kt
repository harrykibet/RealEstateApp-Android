package com.estatia.realestate.apps.core.common.exceptions

sealed class DatabaseException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), InfrastructureException {


    data object PermissionDenied :
        DatabaseException(
            "Database permission denied"
        )


    data object NotFound :
        DatabaseException(
            "Database record not found"
        )


    data object AlreadyExists :
        DatabaseException(
            "Database record already exists"
        )


    data object TransactionFailed :
        DatabaseException(
            "Database transaction failed"
        )

    data class InvalidData(val msg: String) :
        DatabaseException(
            "Database invalid data : $msg"
        )


    data object ResourceExhausted :
        DatabaseException(
            "Database resource exhausted"
        )


    data object Unavailable :
        DatabaseException(
            "Database unavailable"
        ), RetryableException


    data object Timeout :
        DatabaseException(
            "Database timeout"
        )

    data class LocalDatabaseError(val msg: String) :
        DatabaseException(
            "Local database error : $msg"
        )

    data class Unknown(
        val original: Throwable
    ) : DatabaseException(
        "Unknown database error",
        original
    )
}