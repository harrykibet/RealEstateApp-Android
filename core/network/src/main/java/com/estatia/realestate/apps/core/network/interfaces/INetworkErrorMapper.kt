package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.NetworkException

interface INetworkErrorMapper {

    fun map(
        throwable: Throwable
    ): NetworkException
}