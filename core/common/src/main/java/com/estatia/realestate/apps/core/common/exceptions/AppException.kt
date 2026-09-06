package com.estatia.realestate.apps.core.common.exceptions

sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
    data class Unknown(val original: Throwable) : AppException(original.message, original)
}
