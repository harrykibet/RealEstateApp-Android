package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
sealed class RemoteServiceException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data class FirebaseUnknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown Firebase service error",
        cause = original
    )


@DomainModel
    data class Unknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown remote service error",
        cause = original
    )
}
