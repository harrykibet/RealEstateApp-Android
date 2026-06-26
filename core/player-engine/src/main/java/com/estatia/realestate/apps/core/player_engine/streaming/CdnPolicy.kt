package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import javax.inject.Inject
import kotlin.random.Random

class CdnPolicy @Inject constructor(
    private val networkUtils: INetworkUtils,
    private val random: Random
) : ICdnPolicy {

    override suspend fun select(
        endpoints: List<CdnEndpoint>,
        healthMonitor: CdnHealthMonitor
    ): CdnEndpoint {

        if (!networkUtils.isLowLatencyNetwork()) {
            return endpoints.random(random)
        }

        val healthy = endpoints.mapNotNull { endpoint ->
            val health = healthMonitor.getHealth(endpoint)
            if (!health.isCircuitOpen && health.latencyMs != null)
                endpoint to health.latencyMs
            else null
        }

        return healthy.minByOrNull { it.second }?.first
            ?: endpoints.random(random)
    }
}