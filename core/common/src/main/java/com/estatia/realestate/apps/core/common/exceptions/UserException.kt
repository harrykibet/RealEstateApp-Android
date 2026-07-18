package com.estatia.realestate.apps.core.common.exceptions

sealed class UserException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


    data object UserNotFound :
        UserException(
            "User profile not found"
        )


    data object ProfileIncomplete :
        UserException(
            "User profile incomplete"
        )


    data object UserCreationFailed :
        UserException(
            "User creation failed"
        )

    data object PermissionDenied :
        UserException(
            "Permission denied"
        )

    data class Unknown(val throwable: Throwable) :
        UserException(
            "Unknown user error",
            throwable
        )

    data object AlreadyExists :
        UserException(
            "User already exists"
        )



    data object UserUpdateFailed :
        UserException(
            "User update failed"
        )
}