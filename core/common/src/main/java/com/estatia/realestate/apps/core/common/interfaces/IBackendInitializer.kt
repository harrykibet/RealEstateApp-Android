package com.estatia.realestate.apps.core.common.interfaces

/**
 * Interface for backend-specific initialization logic.
 */
interface IBackendInitializer {
    /**
     * Called during application startup to initialize the backend SDKs or observability.
     */
    fun initialize()
}
