package com.application.real_estate_app.core.network_utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

@Suppress("UNUSED")
object NetworkStatus {

    enum class ConnectionType {
        WIFI, CELLULAR, NONE
    }

    sealed class NetworkStatusResult {
        data object Connected : NetworkStatusResult()
        data object PoorConnection : NetworkStatusResult()
        data object NoInternet : NetworkStatusResult()
    }

    private fun getConnectionType(connectivityManager: ConnectivityManager): ConnectionType {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return when {
            networkCapabilities == null -> ConnectionType.NONE
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            else -> ConnectionType.NONE
        }
    }

    private fun isConnected(connectivityManager: ConnectivityManager): Boolean {
        return getConnectionType(connectivityManager) != ConnectionType.NONE
    }

    private fun isPoorConnection(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val linkDownstreamBandwidthKbps = networkCapabilities?.linkDownstreamBandwidthKbps ?: 0
        val linkUpstreamBandwidthKbps = networkCapabilities?.linkUpstreamBandwidthKbps ?: 0

        // Threshold for poor connection in Kbps (adjustable)
        val poorConnectionThresholdKbps = 50 // 50 kilobits per second
        return linkDownstreamBandwidthKbps < poorConnectionThresholdKbps || linkUpstreamBandwidthKbps < poorConnectionThresholdKbps
    }

    /**
     * Checks actual internet access by attempting a socket connection to an external server.
     */
    fun hasInternetAccess(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Checks internet access by pinging a Google DNS server
     *may not be as reliable in some cases where ICMP is blocked
     * */
    fun checkInternetWithPing(): Boolean {
        return try {
            val address = InetAddress.getByName("8.8.8.8")
            address.isReachable(3000) // Timeout in ms
        } catch (e: Exception) {
            false
        }
    }

    fun isVpnConnected(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
    }

    fun getNetworkBandwidth(connectivityManager: ConnectivityManager): Pair<Long, Long> {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val downSpeed = networkCapabilities?.linkDownstreamBandwidthKbps?.toLong() ?: 0L
        val upSpeed = networkCapabilities?.linkUpstreamBandwidthKbps?.toLong() ?: 0L

        return Pair(downSpeed, upSpeed)
    }

    suspend fun getNetworkLatency(host: String): Long {
        return try {
            val start = System.currentTimeMillis()
            val socket = Socket()
            withContext(Dispatchers.IO) {
                socket.connect(InetSocketAddress(host, 80), 1000)
            }
            withContext(Dispatchers.IO) {
                socket.close()
            }
            System.currentTimeMillis() - start
        } catch (e: IOException) {
            -1L
        }
    }

    fun isNetworkMetered(connectivityManager: ConnectivityManager): Boolean {
        return connectivityManager.isActiveNetworkMetered
    }

    fun registerNetworkCallback(connectivityManager: ConnectivityManager, callback: ConnectivityManager.NetworkCallback) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    fun unregisterNetworkCallback(connectivityManager: ConnectivityManager, callback: ConnectivityManager.NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(callback)
    }


    /**
     * Returns a result indicating the network status (Connected, Poor Connection, No Internet).
     */
    fun getNetworkStatus(connectivityManager: ConnectivityManager): NetworkStatusResult {
        return when {
            !isConnected(connectivityManager) -> NetworkStatusResult.NoInternet
            !hasInternetAccess() -> NetworkStatusResult.NoInternet
            isPoorConnection(connectivityManager) -> NetworkStatusResult.PoorConnection
            else -> NetworkStatusResult.Connected
        }
    }
}
