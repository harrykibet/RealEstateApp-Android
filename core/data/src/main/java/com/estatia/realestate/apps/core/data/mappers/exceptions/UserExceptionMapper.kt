package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.UserException
import javax.inject.Inject

/**
 * Specialized mapper for translating user-related infrastructure failures.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map platform errors to [UserException] domain failures.
 * - Concurrency: Stateless and thread-safe.
 */
internal class UserExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<UserException>(
    notFound = { UserException.UserNotFound },
    permissionDenied = { UserException.PermissionDenied },
    creationFailed = { UserException.UserCreationFailed },
    alreadyExists = { UserException.AlreadyExists },
    unknown = { UserException.Unknown(it) }
)
