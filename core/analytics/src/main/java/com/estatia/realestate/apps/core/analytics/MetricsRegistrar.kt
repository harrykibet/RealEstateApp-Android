package com.estatia.realestate.apps.core.analytics

import io.micrometer.core.instrument.MeterRegistry

/**
 * Abstraction for registering [MeterRegistry] instances with the global metrics collector.
 */
interface MetricsRegistrar {
    /**
     * Registers the given [registry].
     */
    fun register(registry: MeterRegistry)
}
