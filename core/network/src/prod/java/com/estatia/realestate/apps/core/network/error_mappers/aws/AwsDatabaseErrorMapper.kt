package com.estatia.realestate.apps.core.network.error_mappers.aws

import com.amplifyframework.api.ApiException
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.interfaces.IDatabaseErrorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AwsDatabaseErrorMapper @Inject constructor() : IDatabaseErrorMapper {

    override fun map(throwable: Throwable): DatabaseException {
        if (throwable !is ApiException) {
            return DatabaseException.Unknown(throwable)
        }

        val message = throwable.message?.lowercase() ?: ""

        return when {
            message.contains("permission denied") || message.contains("not authorized") -> 
                DatabaseException.PermissionDenied
            message.contains("not found") -> 
                DatabaseException.NotFound
            message.contains("already exists") -> 
                DatabaseException.AlreadyExists
            message.contains("limit exceeded") || message.contains("throttled") -> 
                DatabaseException.ResourceExhausted
            message.contains("timeout") -> 
                DatabaseException.Timeout
            else -> DatabaseException.Unknown(throwable)
        }
    }
}
