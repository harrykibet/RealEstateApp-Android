package com.application.real_estate_app.core_interface

import android.net.ConnectivityManager
import com.application.real_estate_app.core_network.NetworkUtils.NetworkStatusResult

interface INetworkUtils {
    fun hasInternetAccess(): Boolean
    fun checkInternetWithPing(): Boolean
    fun isVpnConnected(): Boolean
    fun getNetworkBandwidth(): Pair<Long, Long>
    suspend fun getNetworkLatency(host: String): Long
    fun isLowLatencyNetwork(): Boolean
    fun isNetworkMetered(): Boolean
    fun estimatedThroughputbps(): Long
    fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback)
    fun unregisterNetworkCallback(callback: ConnectivityManager.NetworkCallback)
    fun getNetworkStatus(): NetworkStatusResult
}