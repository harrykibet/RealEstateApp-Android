package com.estatia.realestate.apps.core.player_engine.streaming

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import javax.inject.Inject
import kotlin.random.Random

@UnstableApi
class CdnPolicy @Inject constructor(
    private val environmentCoordinator: EnvironmentCoordinator,
    private val random: Random
) : ICdnPolicy {

    override suspend fun select(
        endpoints: List<CdnEndpoint>,
        healthMonitor: CdnHealthMonitor
    ): CdnEndpoint {

        val env = environmentCoordinator.environment.value

        // 1. Fast path: degraded network → avoid strict optimization
        if (shouldUseRandomFallback(env)) {
            return endpoints.random(random)
        }

        // 2. Evaluate health + latency only when network is stable
        val healthy = endpoints.mapNotNull { endpoint ->
            val health = healthMonitor.getHealth(endpoint)

            if (!health.isCircuitOpen && health.latencyMs != null) {
                endpoint to health.latencyMs
            } else {
                null
            }
        }

        // 3. Best latency wins
        return healthy.minByOrNull { it.second }?.first
            ?: endpoints.random(random)
    }

    /**
     * Centralized decision instead of INetworkUtils.
     * Keeps CDN policy aligned with global environment model.
     */
    private fun shouldUseRandomFallback(env: EnvironmentState): Boolean {
        return env.shouldThrottlePerformance || env.isMetered
    }
}
