package com.estatia.realestate.apps.core.network.core

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject


/**
 * Android implementation of [INetworkStateProvider] using [ConnectivityManager].
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Monitor and report global network connectivity state.
 * - Concurrency: Thread-safe reactive observation via [callbackFlow].
 * - Lifecycle: Automatically cleans up system-level callbacks via [awaitClose].
 * - Resilience: Performs validation checks (NET_CAPABILITY_VALIDATED) to distinguish between 
 *   connected-but-offline (e.g., captive portal) and true internet connectivity.
 */
internal class AndroidNetworkStateProvider @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : INetworkStateProvider {

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    override fun observe(): Flow<NetworkState> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: android.net.Network) {
                trySend(computeState())
            }

            override fun onLost(network: android.net.Network) {
                trySend(computeState())
            }

            override fun onCapabilitiesChanged(
                network: android.net.Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(computeState())
            }
        }

        connectivityManager.registerNetworkCallback(request, callback)

        trySend(computeState())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    override fun current(): NetworkState = computeState()

    private fun computeState(): NetworkState {
        val network = connectivityManager.activeNetwork ?: return NetworkState.NoInternet
        val caps = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkState.NoInternet

        val validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        if (!validated) return NetworkState.NoInternet

        val downstream = caps.linkDownstreamBandwidthKbps
        val upstream = caps.linkUpstreamBandwidthKbps

        return if (downstream < 50 || upstream < 50) {
            NetworkState.PoorConnection
        } else {
            NetworkState.Connected
        }
    }
}
