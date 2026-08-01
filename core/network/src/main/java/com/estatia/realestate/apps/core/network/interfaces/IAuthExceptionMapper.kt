package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException

interface IAuthExceptionMapper {
    fun map(
        throwable: Throwable
    ): AuthException
}
