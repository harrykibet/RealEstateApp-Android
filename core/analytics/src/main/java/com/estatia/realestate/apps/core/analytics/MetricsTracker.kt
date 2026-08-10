package com.estatia.realestate.apps.core.analytics

import com.estatia.realestate.apps.core.domain.interfaces.IMetricsTracker
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Metrics
import io.micrometer.core.instrument.Tag
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Micrometer-based implementation of [IMetricsTracker].
 */
@Singleton
class MetricsTracker @Inject constructor() : IMetricsTracker {

    private val registry: MeterRegistry = Metrics.globalRegistry

    override fun trackDuration(name: String, duration: Duration, tags: Map<String, String>) {
        val micrometerTags = tags.map { Tag.of(it.key, it.value) }
        Timer.builder(name)
            .tags(micrometerTags)
            .register(registry)
            .record(duration.toJavaDuration())
    }

    override fun incrementCounter(name: String, tags: Map<String, String>) {
        val micrometerTags = tags.map { Tag.of(it.key, it.value) }
        Counter.builder(name)
            .tags(micrometerTags)
            .register(registry)
            .increment()
    }
}
