package com.estatia.realestate.apps.core.analytics

import io.micrometer.core.instrument.MeterRegistry
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Abstraction for registering [MeterRegistry] instances with the global metrics collector.
 */
@Contract
interface MetricsRegistrar {
    /**
     * Registers the given [registry].
     */
    fun register(registry: MeterRegistry)
}
