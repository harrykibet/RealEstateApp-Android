package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.player_engine.di.EngineScope
import com.estatia.realestate.apps.core.player_engine.di.IODispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class CdnHealthMonitor @Inject constructor(
    private val latencyMeasurer: ILatencyMeasurer,
    @param:EngineScope private val scope: CoroutineScope,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val ttl: Duration = 30.seconds
    private val timeout: Duration = 3.seconds
    private val failureThreshold: Int = 3
    private val circuitOpenDuration: Duration = 60.seconds
    private val clock: () -> Long = { System.currentTimeMillis() }

    private val endpointMutexes = java.util.concurrent.ConcurrentHashMap<String, Mutex>()
    private val healthMap = java.util.concurrent.ConcurrentHashMap<String, CdnHealth>()

    private fun mutexFor(key: String): Mutex =
        endpointMutexes.getOrPut(key) { Mutex() }

    /**
     * Returns a snapshot of all currently known health metrics.
     */
    fun getHealthSnapshot(): Map<String, CdnHealth> {
        return healthMap.toMap()
    }

    /**
     * Triggers background probes for any endpoints that are missing or stale.
     * Non-blocking.
     */
    fun refreshIfStale(endpoints: List<CdnEndpoint>) {
        endpoints.forEach { endpoint ->
            val current = healthMap[endpoint.baseUrl]
            val now = clock()

            if (current == null || now - current.lastCheckedAt > ttl.inWholeMilliseconds) {
                // Launch background probe
                scope.launch {
                    mutexFor(endpoint.baseUrl).withLock {
                        // Re-check under lock
                        val recheck = healthMap[endpoint.baseUrl]
                        if (recheck == null || clock() - recheck.lastCheckedAt > ttl.inWholeMilliseconds) {
                            val updated = measure(endpoint, recheck)
                            healthMap[endpoint.baseUrl] = updated
                        }
                    }
                }
            }
        }
    }

    /**
     * Non-blocking version of failure reporting.
     */
    fun reportExternalFailureAsync(baseUrl: String) {
        scope.launch {
            reportExternalFailure(baseUrl)
        }
    }

    /**
     * Reports an external failure (like a 500 error or timeout) for a specific endpoint.
     */
    suspend fun reportExternalFailure(baseUrl: String) {
        mutexFor(baseUrl).withLock {
            val previous = healthMap[baseUrl]
            val now = clock()
            
            val failures = (previous?.failureCount ?: 0) + 1
            val circuitOpenUntil = if (failures >= failureThreshold) {
                now + circuitOpenDuration.inWholeMilliseconds
            } else {
                previous?.circuitOpenUntil
            }

            healthMap[baseUrl] = CdnHealth(
                latencyMs = previous?.latencyMs,
                failureCount = failures,
                lastCheckedAt = now,
                circuitOpenUntil = circuitOpenUntil
            )
        }
    }

    private suspend fun measure(
        endpoint: CdnEndpoint,
        previous: CdnHealth?
    ): CdnHealth = withContext(ioDispatcher) {

        val now = clock()

        // circuit breaker open → skip probing
        if (previous?.isCircuitOpen == true &&
            previous.circuitOpenUntil != null &&
            now < previous.circuitOpenUntil
        ) {
            return@withContext previous
        }

        try {
            val latency = latencyMeasurer.measure(endpoint.baseUrl, timeout)

            CdnHealth(
                latencyMs = latency,
                failureCount = 0,
                lastCheckedAt = now,
                circuitOpenUntil = null
            )

        } catch (_: Throwable) {
            val failures = (previous?.failureCount ?: 0) + 1
            val circuitOpenUntil =
                if (failures >= failureThreshold)
                    now + circuitOpenDuration.inWholeMilliseconds
                else null

            CdnHealth(
                latencyMs = null,
                failureCount = failures,
                lastCheckedAt = now,
                circuitOpenUntil = circuitOpenUntil
            )
        }
    }
}
