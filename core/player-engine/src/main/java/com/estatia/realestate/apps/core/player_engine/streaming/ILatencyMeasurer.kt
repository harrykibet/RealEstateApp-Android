package com.estatia.realestate.apps.core.player_engine.streaming

import kotlin.time.Duration
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Utility for measuring network latency to a specific host.
 */
@Contract
interface ILatencyMeasurer {
    /**
     * Measures the Round Trip Time (RTT) to the specified host.
     * 
     * @param host The hostname or IP address to ping.
     * @param timeout Maximum duration to wait for a response.
     * @return Latency in milliseconds, or -1 if the measurement failed.
     */
    suspend fun measure(host: String, timeout: Duration): Long
}
