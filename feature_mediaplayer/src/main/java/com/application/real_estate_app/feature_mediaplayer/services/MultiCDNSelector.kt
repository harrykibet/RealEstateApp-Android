package com.application.real_estate_app.feature_mediaplayer.services

import android.os.SystemClock
import com.application.real_estate_app.core.network.NetworkUtils
import javax.inject.Inject
import javax.inject.Singleton

// Client-side CDN routing logic
@Singleton
class MultiCDNSelector @Inject constructor(
    private val networkUtils: NetworkUtils
) {
    private val cdnEndpoints = listOf(
        "https://cdn1.example.com",
        "https://cdn2.example.com"
    )

    suspend fun selectOptimalCDN(): String {
        return if (networkUtils.isLowLatencyNetwork()) {
            cdnEndpoints.minBy { pingCDN(it) }
        } else {
            cdnEndpoints.random()
        }
    }

    private suspend fun pingCDN(url: String): Long {
       return networkUtils.getNetworkLatency(url)
    }
}