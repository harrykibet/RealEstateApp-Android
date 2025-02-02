package com.application.real_estate_app.core.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.application.real_estate_app.core.domain.interfaces.INetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

@Suppress("UNUSED")
class NetworkUtils @Inject  constructor(
    private val connectivityManager: ConnectivityManager
) : INetworkUtils {

    // Threshold for poor connection in Kbps (adjustable)
    private val THRESHOLD_KBPS = 50 // 50 kilobits per second

    private val GOOGLE_DNS_ADDRESS = "8.8.8.8"
    private val GOOGLE_DNS_PORT = 53
    private val HTTP_PORT = 80

    enum class ConnectionType {
        WIFI, CELLULAR, NONE
    }

    sealed class NetworkStatusResult {
        data object Connected : NetworkStatusResult()
        data object PoorConnection : NetworkStatusResult()
        data object NoInternet : NetworkStatusResult()
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

    private fun isConnected(): Boolean {
        return getConnectionType() != ConnectionType.NONE
    }

    private fun isPoorConnection(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        val linkDownstreamBandwidthKbps = networkCapabilities?.linkDownstreamBandwidthKbps ?: 0
        val linkUpstreamBandwidthKbps = networkCapabilities?.linkUpstreamBandwidthKbps ?: 0

        return linkDownstreamBandwidthKbps < THRESHOLD_KBPS || linkUpstreamBandwidthKbps < THRESHOLD_KBPS
    }

    /**
     * Checks actual internet access by attempting a socket connection to an external server.
     */
    override fun hasInternetAccess(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(GOOGLE_DNS_ADDRESS, GOOGLE_DNS_PORT), 1500)
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
    override fun checkInternetWithPing(): Boolean {
        return try {
            val address = InetAddress.getByName(GOOGLE_DNS_ADDRESS)
            address.isReachable(3000) // Timeout in ms
        } catch (e: Exception) {
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
                socket.connect(InetSocketAddress(host, HTTP_PORT), 1000)
            }
            withContext(Dispatchers.IO) {
                socket.close()
            }
            System.currentTimeMillis() - start
        } catch (e: IOException) {
            -1L
        }
    }

    override fun isNetworkMetered(): Boolean {
        return connectivityManager.isActiveNetworkMetered
    }

    override fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    override fun unregisterNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        connectivityManager.unregisterNetworkCallback(callback)
    }


    /**
     * Returns a result indicating the network status (Connected, Poor Connection, No Internet).
     */
    override fun getNetworkStatus(): NetworkStatusResult {
        return when {
            !isConnected() -> NetworkStatusResult.NoInternet
            !hasInternetAccess() -> NetworkStatusResult.NoInternet
            isPoorConnection() -> NetworkStatusResult.PoorConnection
            else -> NetworkStatusResult.Connected
        }
    }
}
