package com.application.real_estate_app.feature_mediaplayer.services

import com.application.real_estate_app.core.domain.interfaces.INetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Client-side CDN routing logic
@Singleton
class MultiCDNSelector @Inject constructor(
    private val networkUtils: INetworkUtils
) {
    private val cdnEndpoints = listOf(
        "https://cdn1.example.com",
        "https://cdn2.example.com"
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