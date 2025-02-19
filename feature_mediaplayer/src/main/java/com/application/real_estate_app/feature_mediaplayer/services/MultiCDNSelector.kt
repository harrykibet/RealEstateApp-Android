package com.application.real_estate_app.feature_mediaplayer.services

import com.application.real_estate_app.core.domain.interfaces.INetworkUtils
import com.application.real_estate_app.core.domain.interfaces.IRemoteConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Client-side CDN routing logic
@Suppress("Unused")
@Singleton
class MultiCDNSelector @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val remoteConfigManager: IRemoteConfigManager
) {
    private val cdnEndpoints = listOf(
        remoteConfigManager.getCDNEndPoint1(),
        remoteConfigManager.getCDNEndPoint2()
    )

     fun selectOptimalCDN(): String {
        return if (networkUtils.isLowLatencyNetwork()) {
            cdnEndpoints.minBy { pingCDN(it) }
        } else {
            cdnEndpoints.random()
        }
    }

    private fun pingCDN(url: String): Long {
        var latency = 0L
        CoroutineScope(Dispatchers.IO).launch {
            latency = networkUtils.getNetworkLatency(url)
        }
       return latency
    }
}