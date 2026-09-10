package com.estatia.realestate.apps.core.analytics

import io.micrometer.core.instrument.MeterRegistry
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import io.micrometer.core.instrument.Metrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of [MetricsRegistrar] that uses Micrometer's [Metrics.globalRegistry].
 */
@Helper
@Singleton
class MicrometerMetricsRegistrar @Inject constructor() : MetricsRegistrar {
    override fun register(registry: MeterRegistry) {
        Metrics.addRegistry(registry)
    }
}
