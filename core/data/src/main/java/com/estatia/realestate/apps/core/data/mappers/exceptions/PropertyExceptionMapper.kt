package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import javax.inject.Inject

/**
 * Maps infrastructure exceptions to [PropertyException].
 */
internal class PropertyExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<PropertyException>(
    notFound = { PropertyException.PropertyNotFound },
    permissionDenied = { PropertyException.PermissionDenied },
    creationFailed = { PropertyException.PropertyCreationFailed() },
    alreadyExists = { PropertyException.AlreadyExists },
    unknown = { PropertyException.Unknown(it) }
)
