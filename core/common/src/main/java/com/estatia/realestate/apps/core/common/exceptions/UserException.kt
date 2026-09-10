package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Helper

@Helper
sealed class UserException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@Helper
    data object UserNotFound :
        UserException(
            "User profile not found"
        )


@Helper
    data object ProfileIncomplete :
        UserException(
            "User profile incomplete"
        )


@Helper
    data object UserCreationFailed :
        UserException(
            "User creation failed"
        )

@Helper
    data object PermissionDenied :
        UserException(
            "Permission denied"
        )

@Helper
    data class Unknown(val throwable: Throwable) :
        UserException(
            "Unknown user error",
            throwable
        )

@Helper
    data object AlreadyExists :
        UserException(
            "User already exists"
        )



@Helper
    data object UserUpdateFailed :
        UserException(
            "User update failed"
        )
}
