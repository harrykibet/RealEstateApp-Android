package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint

interface ICdnPolicy {
    suspend fun select(
        endpoints: List<CdnEndpoint>,
        healthMonitor: CdnHealthMonitor
    ): CdnEndpoint
}