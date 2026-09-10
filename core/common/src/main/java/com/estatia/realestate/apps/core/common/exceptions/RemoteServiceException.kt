package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class RemoteServiceException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data class FirebaseUnknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown Firebase service error",
        cause = original
    )


@Helper
    data class Unknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown remote service error",
        cause = original
    )
}
