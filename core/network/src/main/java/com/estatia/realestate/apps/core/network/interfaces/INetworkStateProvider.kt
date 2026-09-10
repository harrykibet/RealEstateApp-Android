package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.core.NetworkState
import kotlinx.coroutines.flow.Flow
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Utility for monitoring the device's network connectivity state.
 */
@Contract
interface INetworkStateProvider {
    /**
     * Returns a [Flow] that emits updates whenever the network state changes.
     */
    fun observe(): Flow<NetworkState>

    /**
     * Returns the current [NetworkState] of the device.
     */
    fun current(): NetworkState
}
