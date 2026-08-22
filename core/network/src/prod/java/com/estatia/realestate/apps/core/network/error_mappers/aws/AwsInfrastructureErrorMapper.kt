package com.estatia.realestate.apps.core.network.error_mappers.aws

import com.amplifyframework.AmplifyException
import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.network.interfaces.IInfrastructureErrorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AwsInfrastructureErrorMapper @Inject constructor() : IInfrastructureErrorMapper {

    override fun map(throwable: Throwable): AppException {
        if (throwable !is AmplifyException) {
            return NetworkException.Unknown(throwable)
        }

        return NetworkException.Unknown(throwable) // Generic for now
    }
}
