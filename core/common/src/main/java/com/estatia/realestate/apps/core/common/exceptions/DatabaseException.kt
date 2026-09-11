package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class DatabaseException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), InfrastructureException {


@DomainModel
    data object PermissionDenied :
        DatabaseException(
            "Database permission denied"
        )


@DomainModel
    data object NotFound :
        DatabaseException(
            "Database record not found"
        )


@DomainModel
    data object AlreadyExists :
        DatabaseException(
            "Database record already exists"
        )


@DomainModel
    data object TransactionFailed :
        DatabaseException(
            "Database transaction failed"
        )

@DomainModel
    data class InvalidData(val msg: String) :
        DatabaseException(
            "Database invalid data : $msg"
        )


@DomainModel
    data object ResourceExhausted :
        DatabaseException(
            "Database resource exhausted"
        )


@DomainModel
    data object Unavailable :
        DatabaseException(
            "Database unavailable"
        ), RetryableException


@DomainModel
    data object Timeout :
        DatabaseException(
            "Database timeout"
        ), RetryableException


@DomainModel
    data class LocalDatabaseError(val msg: String) :
        DatabaseException(
            "Local database error : $msg"
        )

@DomainModel
    data class ConstraintViolation(
        override val cause: Throwable
    ) : DatabaseException(
        "Database constraint violated",
        cause
    )

@DomainModel
    data class CorruptedDatabase(
        override val cause: Throwable
    ) : DatabaseException(
        "Database is corrupted",
        cause
    )

@DomainModel
    data class DiskIO(
        override val cause: Throwable
    ) : DatabaseException(
        "Database disk I/O failure",
        cause
    )

@DomainModel
    data class StorageFull(
        override val cause: Throwable
    ) : DatabaseException(
        "Device storage is full",
        cause
    )

@DomainModel
    data class QueryFailed(
        override val cause: Throwable
    ) : DatabaseException(
        "Database query failed",
        cause
    )

@DomainModel
    data class Unknown(
        val original: Throwable
    ) : DatabaseException(
        "Unknown database error",
        original
    )
}
