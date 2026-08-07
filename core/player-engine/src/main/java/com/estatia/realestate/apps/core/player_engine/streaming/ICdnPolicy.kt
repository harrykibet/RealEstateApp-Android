package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint

/**
 * Policy for selecting the optimal CDN endpoint from a list of available candidates.
 */
interface ICdnPolicy {
    /**
     * Selects an endpoint based on health metrics and latency data.
     * 
     * @param endpoints List of available CDN endpoints.
     * @param healthMonitor Monitor providing real-time health and latency metrics.
     * @return The selected [CdnEndpoint].
     */
    suspend fun select(
        endpoints: List<CdnEndpoint>,
        healthMonitor: CdnHealthMonitor
    ): CdnEndpoint
}
