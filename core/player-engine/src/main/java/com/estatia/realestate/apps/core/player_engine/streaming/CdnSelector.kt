package com.estatia.realestate.apps.core.player_engine.streaming

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CdnSelector @Inject constructor(
    private val policy: ICdnPolicy,
    private val healthMonitor: CdnHealthMonitor,
    private val endpointsProvider: () -> List<CdnEndpoint>
) {

    suspend fun select(): CdnEndpoint {
        val endpoints = endpointsProvider()

        require(endpoints.isNotEmpty()) {
            "No CDN endpoints configured"
        }

        return policy.select(endpoints, healthMonitor)
    }
}