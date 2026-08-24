package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import javax.inject.Inject

/**
 * Specialized mapper for translating property-related infrastructure failures.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map platform errors (Firestore/Room) to [PropertyException] domain failures.
 * - Concurrency: Stateless and thread-safe.
 */
internal class PropertyExceptionMapper @Inject constructor() : BaseInfraExceptionMapper<PropertyException>(
    notFound = { PropertyException.PropertyNotFound },
    permissionDenied = { PropertyException.PermissionDenied },
    creationFailed = { PropertyException.PropertyCreationFailed() },
    alreadyExists = { PropertyException.AlreadyExists },
    unknown = { PropertyException.Unknown(it) }
)
