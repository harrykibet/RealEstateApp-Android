package com.estatia.realestate.apps.core.player_engine.streaming

data class CdnHealth(
    val latencyMs: Long?,
    val failureCount: Int,
    val lastCheckedAt: Long,
    val circuitOpenUntil: Long?
) {
    val isCircuitOpen: Boolean
        get() = circuitOpenUntil != null && System.currentTimeMillis() < circuitOpenUntil
}