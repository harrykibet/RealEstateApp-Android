package com.estatia.realestate.apps.core.common.exceptions

sealed class PropertyException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object PropertyNotFound :
        PropertyException(
            "Property not found"
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