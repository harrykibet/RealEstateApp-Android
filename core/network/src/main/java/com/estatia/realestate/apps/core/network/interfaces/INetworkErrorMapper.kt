package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

@DataSource
interface INetworkErrorMapper {

    fun map(
        throwable: Throwable
    ): NetworkException
}
