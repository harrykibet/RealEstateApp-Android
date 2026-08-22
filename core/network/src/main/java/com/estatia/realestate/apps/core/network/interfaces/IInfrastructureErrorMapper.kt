package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException

interface IInfrastructureErrorMapper {
    fun map(throwable: Throwable): AppException
}
