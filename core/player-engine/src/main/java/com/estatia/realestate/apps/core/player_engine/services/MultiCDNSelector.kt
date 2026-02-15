package com.estatia.realestate.apps.core.player_engine.services

import com.estatia.realestate.apps.core.network.interfaces.IRemoteConfigManager
import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Client-side CDN routing logic
@Singleton
class MultiCDNSelector @Inject constructor(
    private val networkUtils: INetworkUtils,
    remoteConfigManager: IRemoteConfigManager
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