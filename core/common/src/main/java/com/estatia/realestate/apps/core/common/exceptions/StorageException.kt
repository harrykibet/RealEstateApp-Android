package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class StorageException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), InfrastructureException {


@Helper
    data object PermissionDenied :
        StorageException(
            "Storage permission denied"
        )


@Helper
    data object ObjectNotFound :
        StorageException(
            "Storage object not found"
        )


@Helper
    data object BucketNotFound :
        StorageException(
            "Storage bucket not found"
        )

@Helper
    data object UploadFailed :
        StorageException(
            "Storage upload failed"
        )


@Helper
    data object QuotaExceeded :
        StorageException(
            "Storage quota exceeded"
        )


@Helper
    data object Unauthenticated :
        StorageException(
            "Storage authentication required"
        )


@Helper
    data object RetryLimitExceeded :
        StorageException(
            "Storage retry limit exceeded"
        )


@Helper
    data object Cancelled :
        StorageException(
            "Storage operation cancelled"
        )


@Helper
    data object Unavailable :
        StorageException(
            "Storage service unavailable"
        )


@Helper
    data class Unknown(
        val original: Throwable
    ) : StorageException(
        message = "Unknown storage error",
        cause = original
    )
}
