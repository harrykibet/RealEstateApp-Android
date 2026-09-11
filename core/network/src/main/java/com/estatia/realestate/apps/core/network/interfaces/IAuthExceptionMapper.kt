package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

@DataSource
interface IAuthExceptionMapper {
    fun map(
        throwable: Throwable
    ): AuthException
}
