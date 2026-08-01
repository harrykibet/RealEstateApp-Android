package com.estatia.realestate.apps.core.common.exceptions

sealed class RemoteServiceException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data class FirebaseUnknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown Firebase service error",
        cause = original
    )


    data class Unknown(
        val original: Throwable
    ) : RemoteServiceException(
        message = "Unknown remote service error",
        cause = original
    )
}
