package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.DomainModel

@DomainModel
sealed class PropertyException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object PropertyNotFound :
        PropertyException(
            "Property not found"
        )

@DomainModel
    data object PermissionDenied :
        PropertyException(
            "Permission denied"
        )

@DomainModel
    data class PropertyDraftNotFound(val msg: String? = null) :
        PropertyException(
            "Property draft not found : $msg"
        )


@DomainModel
    data object AlreadyExists :
        PropertyException(
            "Property already exists"
        )

@DomainModel
    data class SafetyViolation(val reason: String) :
        PropertyException(
            "Content safety violation: $reason"
        )

@DomainModel
    data class Unknown(
        val throwable: Throwable? = null
    ) : PropertyException(
        "Unknown property error",
        throwable
    )


@DomainModel
    data object InvalidProperty :
        PropertyException(
            "Invalid property data"
        )


@DomainModel
    data class PropertyCreationFailed(val msg: String? = null) :
        PropertyException(
            "Property creation failed : $msg"
        )


@DomainModel
    data object PropertyUpdateFailed :
        PropertyException(
            "Property update failed"
        )


@DomainModel
    data object PropertyDeletionFailed :
        PropertyException(
            "Property deletion failed"
        )
}
