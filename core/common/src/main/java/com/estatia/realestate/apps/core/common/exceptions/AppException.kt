package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause) {
@DomainModel
    data class Unknown(val original: Throwable) : AppException(original.message, original)
}
