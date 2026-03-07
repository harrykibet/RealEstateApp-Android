package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.common.interfaces.INetworkUtils
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
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
    private val networkUtils: INetworkUtils,
    @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val ttl: Duration = 30.seconds,
    private val timeout: Duration = 3.seconds,
    private val failureThreshold: Int = 3,
    private val circuitOpenDuration: Duration = 60.seconds,
    private val clock: () -> Long = { System.currentTimeMillis() }
) {

    private val mutex = Mutex()

    private val healthMap = mutableMapOf<String, CdnHealth>()

    suspend fun getHealth(endpoint: CdnEndpoint): CdnHealth =
        mutex.withLock {
            val current = healthMap[endpoint.baseUrl]
            val now = clock()

            if (current != null && now - current.lastCheckedAt < ttl.inWholeMilliseconds) {
                return current
            }

            val updated = measure(endpoint, current)
            healthMap[endpoint.baseUrl] = updated
            updated
        }

    private suspend fun measure(
        endpoint: CdnEndpoint,
        previous: CdnHealth?
    ): CdnHealth = withContext(ioDispatcher) {

        val now = clock()

        if (previous?.isCircuitOpen == true) {
            return@withContext previous
        }

        try {
            val latency = withTimeout(timeout) {
                networkUtils.getNetworkLatency(endpoint.baseUrl)
            }

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