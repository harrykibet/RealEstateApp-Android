package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint

/**
 * Policy for selecting the optimal CDN endpoint from a list of available candidates.
 */
interface ICdnPolicy {
    /**
     * Selects an endpoint based on a snapshot of health metrics.
     * This function is non-suspending to ensure it never blocks the player startup path.
     * 
     * @param endpoints List of available CDN endpoints.
     * @param healthSnapshot Map of endpoint base URLs to their current health metrics.
     * @return The selected [CdnEndpoint].
     */
    fun select(
        endpoints: List<CdnEndpoint>,
        healthSnapshot: Map<String, CdnHealth>
    ): CdnEndpoint
}
