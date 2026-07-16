package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AuthException

interface IFirebaseAuthErrorMapper {
    fun map(
        throwable: Throwable
    ): AuthException
}