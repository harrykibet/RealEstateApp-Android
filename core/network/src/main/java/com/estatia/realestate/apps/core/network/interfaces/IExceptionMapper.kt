package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppException

interface IExceptionMapper {

    fun map(
        throwable: Throwable
    ): AppException
}
