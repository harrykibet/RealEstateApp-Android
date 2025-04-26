package com.application.real_estate_app.core.utils.network


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


private const val BANDWIDTH_THRESHOLD_KBPS = 50 // 50 kilobits per second
private const val GOOGLE_DNS_ADDRESS = "8.8.8.8"
private const val GOOGLE_DNS_PORT = 53
private const val HTTP_PORT = 80


