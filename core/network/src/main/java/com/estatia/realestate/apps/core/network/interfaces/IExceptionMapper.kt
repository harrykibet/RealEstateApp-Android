package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException
import com.estatia.realestate.apps.core.architecture.annotations.Identity.DataSource

@DataSource
interface IExceptionMapper {

    fun map(
        throwable: Throwable
    ): AppException
}
