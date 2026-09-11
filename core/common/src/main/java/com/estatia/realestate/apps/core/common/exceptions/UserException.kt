package com.estatia.realestate.apps.core.common.exceptions

import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel

@DomainModel
sealed class UserException(
    message: String,
    cause: Throwable? = null
) : AppException(message, cause) {


@DomainModel
    data object UserNotFound :
        UserException(
            "User profile not found"
        )


@DomainModel
    data object ProfileIncomplete :
        UserException(
            "User profile incomplete"
        )


@DomainModel
    data object UserCreationFailed :
        UserException(
            "User creation failed"
        )

@DomainModel
    data object PermissionDenied :
        UserException(
            "Permission denied"
        )

@DomainModel
    data class Unknown(val throwable: Throwable) :
        UserException(
            "Unknown user error",
            throwable
        )

@DomainModel
    data object AlreadyExists :
        UserException(
            "User already exists"
        )



@DomainModel
    data object UserUpdateFailed :
        UserException(
            "User update failed"
        )
}
