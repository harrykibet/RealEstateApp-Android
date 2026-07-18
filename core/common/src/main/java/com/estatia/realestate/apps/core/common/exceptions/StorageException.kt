package com.estatia.realestate.apps.core.common.exceptions

sealed class StorageException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause), DomainMappableException {


    data object PermissionDenied :
        StorageException(
            "Storage permission denied"
        )


    data object ObjectNotFound :
        StorageException(
            "Storage object not found"
        )


    data object BucketNotFound :
        StorageException(
            "Storage bucket not found"
        )

    data object UploadFailed :
        StorageException(
            "Storage upload failed"
        )


    data object QuotaExceeded :
        StorageException(
            "Storage quota exceeded"
        )


    data object Unauthenticated :
        StorageException(
            "Storage authentication required"
        )


    data object RetryLimitExceeded :
        StorageException(
            "Storage retry limit exceeded"
        )


    data object Cancelled :
        StorageException(
            "Storage operation cancelled"
        )


    data object Unavailable :
        StorageException(
            "Storage service unavailable"
        )


    data class Unknown(
        val original: Throwable
    ) : StorageException(
        message = "Unknown storage error",
        cause = original
    )
}