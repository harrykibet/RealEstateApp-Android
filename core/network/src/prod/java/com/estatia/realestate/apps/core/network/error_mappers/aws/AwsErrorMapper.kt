package com.estatia.realestate.apps.core.network.error_mappers.aws

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import javax.inject.Inject

/**
 * AWS implementation of error mapping (Skeleton).
 */
internal class AwsErrorMapper @Inject constructor() {
    fun map(throwable: Throwable): AppException = NetworkException.Unknown(throwable)
}
