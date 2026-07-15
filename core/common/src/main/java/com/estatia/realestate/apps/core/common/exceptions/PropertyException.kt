package com.estatia.realestate.apps.core.common.exceptions

sealed class PropertyException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object PropertyNotFound :
        PropertyException(
            "Property not found"
        )

    data object PermissionDenied :
        PropertyException(
            "Permission denied"
        )

    data object AlreadyExists :
        PropertyException(
            "Property already exists"
        )

    data class Unknown(val throwable: Throwable) :
        PropertyException(
            "Unknown property error",
            throwable
        )


    data object InvalidProperty :
        PropertyException(
            "Invalid property data"
        )


    data object PropertyCreationFailed :
        PropertyException(
            "Property creation failed"
        )


    data object PropertyUpdateFailed :
        PropertyException(
            "Property update failed"
        )


    data object PropertyDeletionFailed :
        PropertyException(
            "Property deletion failed"
        )
}