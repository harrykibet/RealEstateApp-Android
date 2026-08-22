package com.estatia.realestate.apps.core.domain.analytics

import kotlin.time.Duration

/**
 * Interface for tracking structured performance metrics and counters.
 */
interface IMetricsTracker {

    /**
     * Records a duration for a specific timed event.
     */
    fun trackDuration(
        name: String,
        duration: Duration,
        tags: Map<String, String> = emptyMap()
    )

    /**
     * Increments a counter for a specific event.
     */
    fun incrementCounter(
        name: String,
        tags: Map<String, String> = emptyMap()
    )
}
