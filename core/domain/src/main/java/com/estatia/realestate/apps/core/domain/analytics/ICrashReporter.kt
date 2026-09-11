package com.estatia.realestate.apps.core.domain.analytics
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

/**
 * Interface for reporting crashes and logging non-fatal exceptions.
 */
@Contract
interface ICrashReporter {

    /**
     * Logs a message to the crash reporting service.
     */
    fun log(message: String)

    /**
     * Records a non-fatal exception.
     */
    fun recordException(throwable: Throwable)

    /**
     * Sets a custom key-value pair for additional context in crash reports.
     */
    fun setCustomKey(key: String, value: String)
}
