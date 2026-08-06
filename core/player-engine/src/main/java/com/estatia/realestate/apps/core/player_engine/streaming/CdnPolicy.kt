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

        if (shouldUseRandomFallback(env)) {
            return endpoints.random(random)
        }

        val scored = endpoints.mapNotNull { endpoint ->
            val health = healthMonitor.getHealth(endpoint)
            if (health.isCircuitOpen || health.latencyMs == null) {
                null
            } else {
                val latencyPenalty = health.latencyMs + (health.failureCount * 250L)
                endpoint to latencyPenalty
            }
        }

        return scored.minByOrNull { it.second }?.first ?: endpoints.random(random)
    }

    private fun shouldUseRandomFallback(env: EnvironmentState): Boolean {
        return env.shouldThrottlePerformance || env.isMetered
    }
}
