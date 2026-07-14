package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.domain.exceptions.NetworkException

interface INetworkErrorMapper {

    fun map(
        throwable: Throwable
    ): NetworkException
}