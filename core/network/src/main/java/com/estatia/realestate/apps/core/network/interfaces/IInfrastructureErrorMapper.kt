package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

@DataSource
interface IInfrastructureErrorMapper {
    fun map(throwable: Throwable): AppException
}
