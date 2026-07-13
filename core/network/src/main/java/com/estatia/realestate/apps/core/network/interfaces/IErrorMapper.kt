package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.exceptions.NetworkException

interface IErrorMapper {

    fun map(
        throwable: Throwable
    ): NetworkException
}