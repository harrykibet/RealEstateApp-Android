package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class PropertyException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object PropertyNotFound :
        PropertyException(
            "Property not found"
        )

@Helper
    data object PermissionDenied :
        PropertyException(
            "Permission denied"
        )

@Helper
    data class PropertyDraftNotFound(val msg: String? = null) :
        PropertyException(
            "Property draft not found : $msg"
        )


@Helper
    data object AlreadyExists :
        PropertyException(
            "Property already exists"
        )

@Helper
    data class SafetyViolation(val reason: String) :
        PropertyException(
            "Content safety violation: $reason"
        )

@Helper
    data class Unknown(
        val throwable: Throwable? = null
    ) : PropertyException(
        "Unknown property error",
        throwable
    )


@Helper
    data object InvalidProperty :
        PropertyException(
            "Invalid property data"
        )


@Helper
    data class PropertyCreationFailed(val msg: String? = null) :
        PropertyException(
            "Property creation failed : $msg"
        )


@Helper
    data object PropertyUpdateFailed :
        PropertyException(
            "Property update failed"
        )


@Helper
    data object PropertyDeletionFailed :
        PropertyException(
            "Property deletion failed"
        )
}
