package com.application.real_estate_app.core.domain.interfaces

import android.net.ConnectivityManager
import com.application.real_estate_app.core.network.NetworkUtils.NetworkStatusResult

interface INetworkUtils {
    fun hasInternetAccess(): Boolean
    fun checkInternetWithPing(): Boolean
    fun isVpnConnected(): Boolean
    fun getNetworkBandwidth(): Pair<Long, Long>
    suspend fun getNetworkLatency(host: String): Long
    fun isNetworkMetered(): Boolean
    fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback)
    fun unregisterNetworkCallback(callback: ConnectivityManager.NetworkCallback)
    fun getNetworkStatus(): NetworkStatusResult
}