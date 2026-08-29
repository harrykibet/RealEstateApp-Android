package com.estatia.realestate.apps.core.player_engine.streaming

import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import javax.inject.Inject
import kotlin.random.Random

@UnstableApi
class CdnPolicy @Inject constructor(
    private val environmentCoordinator: EnvironmentCoordinator,
    private val random: Random,
    private val clock: () -> Long = { System.currentTimeMillis() }
) : ICdnPolicy {

    override fun select(
        endpoints: List<CdnEndpoint>,
        healthSnapshot: Map<String, CdnHealth>
    ): CdnEndpoint {
        val env = environmentCoordinator.environment.value
        val now = clock()

        if (shouldUseRandomFallback(env)) {
            return endpoints.random(random)
        }

        val scored = endpoints.mapNotNull { endpoint ->
            val health = healthSnapshot[endpoint.baseUrl]
            if (health == null || health.isCircuitOpen(now) || health.latencyMs == null) {
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
