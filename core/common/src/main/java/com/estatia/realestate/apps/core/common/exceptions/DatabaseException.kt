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
        ), RetryableException


    data class LocalDatabaseError(val msg: String) :
        DatabaseException(
            "Local database error : $msg"
        )

    data class ConstraintViolation(
        override val cause: Throwable
    ) : DatabaseException(
        "Database constraint violated",
        cause
    )

    data class CorruptedDatabase(
        override val cause: Throwable
    ) : DatabaseException(
        "Database is corrupted",
        cause
    )

    data class DiskIO(
        override val cause: Throwable
    ) : DatabaseException(
        "Database disk I/O failure",
        cause
    )

    data class StorageFull(
        override val cause: Throwable
    ) : DatabaseException(
        "Device storage is full",
        cause
    )

    data class QueryFailed(
        override val cause: Throwable
    ) : DatabaseException(
        "Database query failed",
        cause
    )

    data class Unknown(
        val original: Throwable
    ) : DatabaseException(
        "Unknown database error",
        original
    )
}
