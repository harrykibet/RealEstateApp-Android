package com.estatia.realestate.apps.core.domain.interfaces

/**
 * Interface for reporting crashes and logging non-fatal exceptions.
 */
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
