package com.estatia.realestate.apps.core.data.mappers.exceptions

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.exceptions.InfrastructureException
import com.estatia.realestate.apps.core.common.exceptions.StorageException
import com.estatia.realestate.apps.core.domain.interfaces.DataExceptionMapper

/**
 * Base class for mapping infrastructure exceptions (Database and Storage) to domain-specific exceptions.
 * 
 * @param notFound Lambda returning the domain exception for a "Not Found" error.
 * @param permissionDenied Lambda returning the domain exception for a "Permission Denied" error.
 * @param creationFailed Lambda returning the domain exception for a "Creation Failed" error.
 * @param alreadyExists Optional lambda for an "Already Exists" error.
 * @param unknown Lambda taking the original throwable and returning a generic domain exception.
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
