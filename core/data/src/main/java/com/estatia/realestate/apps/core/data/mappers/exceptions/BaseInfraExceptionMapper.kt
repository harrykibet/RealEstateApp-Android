package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.domain.common.DataExceptionMapper

/**
 * Base class for mapping infrastructure exceptions (Database and Storage) to domain-specific exceptions.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Standardize the translation of platform errors (SQL, Storage) into domain business failures.
 * - Concurrency: Stateless and thread-safe.
 * - Resilience: Provides comprehensive mapping for all [InfrastructureException] subtypes.
 */
abstract class BaseInfraExceptionMapper<T : AppException>(
    private val notFound: () -> T,
    private val permissionDenied: () -> T,
    private val creationFailed: () -> T,
    private val alreadyExists: (() -> T)? = null,
    private val unknown: (Throwable) -> T
) : DataExceptionMapper<T> {

    override fun map(exception: InfrastructureException): T {
        return when (exception) {
            is DatabaseException -> mapDatabaseException(exception)
            is StorageException -> mapStorageException(exception)
        }
    }

    private fun mapDatabaseException(exception: DatabaseException): T {
        return when (exception) {
            DatabaseException.NotFound -> notFound()
            DatabaseException.PermissionDenied -> permissionDenied()
            DatabaseException.AlreadyExists -> alreadyExists?.invoke() ?: unknown(exception)
            else -> unknown(exception)
        }
    }

    private fun mapStorageException(exception: StorageException): T {
        return when (exception) {
            StorageException.UploadFailed -> creationFailed()
            StorageException.PermissionDenied -> permissionDenied()
            StorageException.ObjectNotFound -> notFound()
            else -> unknown(exception)
        }
    }
}
