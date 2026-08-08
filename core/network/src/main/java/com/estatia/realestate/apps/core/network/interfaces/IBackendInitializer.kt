package com.estatia.realestate.apps.core.network.interfaces

/**
 * Interface for backend-specific initialization logic.
 */
interface IBackendInitializer {
    /**
     * Called during application startup to initialize the backend SDKs.
     */
    fun initialize()
}
