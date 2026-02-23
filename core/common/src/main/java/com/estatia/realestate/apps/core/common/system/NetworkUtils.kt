package com.estatia.realestate.apps.core.common.system

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class NetworkUtils @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : INetworkUtils {

    enum class ConnectionType {
        WIFI, CELLULAR, NONE
    }

    private val bandwidth = 50 // 50 kilobits per second
    private val dnsAddress = "8.8.8.8"
    private val port = 80

    data class NetworkStatusResult(
        val state: State,
        val isMetered: Boolean
    ) {
        enum class State {
            CONNECTED,
            POOR_CONNECTION,
            NO_INTERNET
        }
    }


    private fun getConnectionType(): ConnectionType {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return when {
            networkCapabilities == null -> ConnectionType.NONE
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            else -> ConnectionType.NONE
        }
    }

    private fun isPoorConnection(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val linkDownstreamBandwidthKbps = networkCapabilities?.linkDownstreamBandwidthKbps ?: 0
        val linkUpstreamBandwidthKbps = networkCapabilities?.linkUpstreamBandwidthKbps ?: 0

        return linkDownstreamBandwidthKbps < bandwidth || linkUpstreamBandwidthKbps < bandwidth
    }


    override fun checkInternetWithPing(): Boolean {
        return try {
            val address = InetAddress.getByName(dnsAddress)
            address.isReachable(3000) // Timeout in ms
        } catch (_: Exception) {
            false
        }
    }

    override fun isVpnConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    }

    override fun getNetworkBandwidth(): Pair<Long, Long> {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val downSpeed = networkCapabilities?.linkDownstreamBandwidthKbps?.toLong() ?: 0L
        val upSpeed = networkCapabilities?.linkUpstreamBandwidthKbps?.toLong() ?: 0L

        return Pair(downSpeed, upSpeed)
    }

    override suspend fun getNetworkLatency(host: String): Long {
        return try {
            val start = System.currentTimeMillis()
            val socket = Socket()
            withContext(Dispatchers.IO) {
                socket.connect(InetSocketAddress(host, port), 1000)
            }
            withContext(Dispatchers.IO) {
                socket.close()
            }
            System.currentTimeMillis() - start
        } catch (_: IOException) {
            -1L
        }
    }

    override fun observeNetworkStatus(): Flow<NetworkStatusResult> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: android.net.Network) {
                trySend(getNetworkStatus())
            }

            override fun onLost(network: android.net.Network) {
                trySend(getNetworkStatus())
            }

            override fun onCapabilitiesChanged(
                network: android.net.Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(getNetworkStatus())
            }
        }

        registerNetworkCallback(callback)

        // Emit initial state
        trySend(getNetworkStatus())

        awaitClose {
            unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun getEstimatedThroughput(): Long {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        if (capabilities != null) {
            val downSpeedKbps = capabilities.linkDownstreamBandwidthKbps.toLong()
            val upSpeedKbps = capabilities.linkUpstreamBandwidthKbps.toLong()

            // Convert Kbps to bps and return total throughput
            return (downSpeedKbps + upSpeedKbps) * 1000
        }

        return 0L // No network available
    }


    override fun estimatedThroughputbps(): Long {
        val trafficStatsThroughput = getEstimatedThroughput()
        val (downSpeedKbps, upSpeedKbps) = getNetworkBandwidth()

        val networkCapabilitiesThroughput =
            (downSpeedKbps + upSpeedKbps) * 1000 // Convert Kbps to bps

        return if (trafficStatsThroughput > 0) trafficStatsThroughput else networkCapabilitiesThroughput
    }


    override fun isLowLatencyNetwork(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        val isFastNetworkType =
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET
            ) || (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) && (capabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED
            ) || capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)))


        // Check if the network is low latency by analyzing additional properties or capabilities
        val isLowLatency =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )

        return isFastNetworkType && isLowLatency
    }


    override fun isNetworkMetered(): Boolean {
        return connectivityManager.isActiveNetworkMetered
    }


    override fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val request =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    override fun unregisterNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(callback)
    }


    override fun getNetworkStatus(): NetworkStatusResult {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        val isMetered = connectivityManager.isActiveNetworkMetered

        val state = when {
            capabilities == null -> NetworkStatusResult.State.NO_INTERNET
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ->
                NetworkStatusResult.State.NO_INTERNET
            isPoorConnection() ->
                NetworkStatusResult.State.POOR_CONNECTION
            else ->
                NetworkStatusResult.State.CONNECTED
        }

        return NetworkStatusResult(
            state = state,
            isMetered = isMetered
        )
    }
}