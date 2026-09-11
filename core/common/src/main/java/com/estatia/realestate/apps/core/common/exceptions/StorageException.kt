package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class StorageException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), InfrastructureException {


@DomainModel
    data object PermissionDenied :
        StorageException(
            "Storage permission denied"
        )


@DomainModel
    data object ObjectNotFound :
        StorageException(
            "Storage object not found"
        )


@DomainModel
    data object BucketNotFound :
        StorageException(
            "Storage bucket not found"
        )

@DomainModel
    data object UploadFailed :
        StorageException(
            "Storage upload failed"
        )


@DomainModel
    data object QuotaExceeded :
        StorageException(
            "Storage quota exceeded"
        )


@DomainModel
    data object Unauthenticated :
        StorageException(
            "Storage authentication required"
        )


@DomainModel
    data object RetryLimitExceeded :
        StorageException(
            "Storage retry limit exceeded"
        )


@DomainModel
    data object Cancelled :
        StorageException(
            "Storage operation cancelled"
        )


@DomainModel
    data object Unavailable :
        StorageException(
            "Storage service unavailable"
        )


@DomainModel
    data class Unknown(
        val original: Throwable
    ) : StorageException(
        message = "Unknown storage error",
        cause = original
    )
}
