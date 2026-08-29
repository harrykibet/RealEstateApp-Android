package com.estatia.realestate.apps.core.player_engine.streaming

data class CdnHealth(
    val latencyMs: Long?,
    val failureCount: Int,
    val lastCheckedAt: Long,
    val circuitOpenUntil: Long?
) {
    fun isCircuitOpen(now: Long): Boolean =
        circuitOpenUntil != null && now < circuitOpenUntil

    // Legacy property for backward compatibility where clock is not available
    val isCircuitOpen: Boolean
        get() = isCircuitOpen(System.currentTimeMillis())
}
