package com.estatia.realestate.apps.core.player_engine.streaming

interface ICdnPolicy {
    suspend fun select(
        endpoints: List<CdnEndpoint>,
        healthMonitor: CdnHealthMonitor
    ): CdnEndpoint
}