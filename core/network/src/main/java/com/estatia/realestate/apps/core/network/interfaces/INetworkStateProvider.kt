package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.core.NetworkState
import kotlinx.coroutines.flow.Flow

interface INetworkStateProvider {
    fun observe(): Flow<NetworkState>
    fun current(): NetworkState
}
