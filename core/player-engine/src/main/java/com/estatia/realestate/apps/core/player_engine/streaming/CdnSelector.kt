package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.domain.config.INetworkConfig
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CdnSelector @Inject constructor(
    private val policy: ICdnPolicy,
    private val healthMonitor: CdnHealthMonitor,
    private val config: INetworkConfig
) {

    /**
     * Selects an endpoint immediately.
     * Triggers background health refreshes if data is stale.
     */
    fun select(): CdnEndpoint {
        val endpoints = config.cdnEndpoints

        require(endpoints.isNotEmpty()) {
            "No CDN endpoints configured"
        }

        // Trigger background refresh for next time
        healthMonitor.refreshIfStale(endpoints)

        // Immediate selection based on current snapshot
        return policy.select(endpoints, healthMonitor.getHealthSnapshot())
    }
}
