package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class DatabaseException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), InfrastructureException {


@Helper
    data object PermissionDenied :
        DatabaseException(
            "Database permission denied"
        )


@Helper
    data object NotFound :
        DatabaseException(
            "Database record not found"
        )


@Helper
    data object AlreadyExists :
        DatabaseException(
            "Database record already exists"
        )


@Helper
    data object TransactionFailed :
        DatabaseException(
            "Database transaction failed"
        )

@Helper
    data class InvalidData(val msg: String) :
        DatabaseException(
            "Database invalid data : $msg"
        )


@Helper
    data object ResourceExhausted :
        DatabaseException(
            "Database resource exhausted"
        )


@Helper
    data object Unavailable :
        DatabaseException(
            "Database unavailable"
        ), RetryableException


@Helper
    data object Timeout :
        DatabaseException(
            "Database timeout"
        ), RetryableException


@Helper
    data class LocalDatabaseError(val msg: String) :
        DatabaseException(
            "Local database error : $msg"
        )

@Helper
    data class ConstraintViolation(
        override val cause: Throwable
    ) : DatabaseException(
        "Database constraint violated",
        cause
    )

@Helper
    data class CorruptedDatabase(
        override val cause: Throwable
    ) : DatabaseException(
        "Database is corrupted",
        cause
    )

@Helper
    data class DiskIO(
        override val cause: Throwable
    ) : DatabaseException(
        "Database disk I/O failure",
        cause
    )

@Helper
    data class StorageFull(
        override val cause: Throwable
    ) : DatabaseException(
        "Device storage is full",
        cause
    )

@Helper
    data class QueryFailed(
        override val cause: Throwable
    ) : DatabaseException(
        "Database query failed",
        cause
    )

@Helper
    data class Unknown(
        val original: Throwable
    ) : DatabaseException(
        "Unknown database error",
        original
    )
}
