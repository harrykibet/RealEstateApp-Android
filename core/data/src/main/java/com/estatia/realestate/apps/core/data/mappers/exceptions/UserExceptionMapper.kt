package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.UserException
import javax.inject.Inject

/**
 * Maps infrastructure exceptions to [UserException].
 */
internal class UserExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<UserException>(
    notFound = { UserException.UserNotFound },
    permissionDenied = { UserException.PermissionDenied },
    creationFailed = { UserException.UserCreationFailed },
    alreadyExists = { UserException.AlreadyExists },
    unknown = { UserException.Unknown(it) }
)
