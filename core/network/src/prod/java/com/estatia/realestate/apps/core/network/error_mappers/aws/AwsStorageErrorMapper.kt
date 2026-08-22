package com.estatia.realestate.apps.core.network.error_mappers.aws

import com.amplifyframework.storage.StorageException
import com.estatia.realestate.apps.core.common.exceptions.StorageException as DomainStorageException
import com.estatia.realestate.apps.core.network.interfaces.IStorageErrorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AwsStorageErrorMapper @Inject constructor() : IStorageErrorMapper {

    override fun map(throwable: Throwable): DomainStorageException {
        if (throwable !is StorageException) {
            return DomainStorageException.Unknown(throwable)
        }

        val message = throwable.message?.lowercase() ?: ""

        return when {
            message.contains("not found") -> DomainStorageException.ObjectNotFound
            message.contains("permission denied") || message.contains("access denied") -> 
                DomainStorageException.PermissionDenied
            message.contains("unauthenticated") -> DomainStorageException.Unauthenticated
            message.contains("limit exceeded") -> DomainStorageException.QuotaExceeded
            else -> DomainStorageException.Unknown(throwable)
        }
    }
}
