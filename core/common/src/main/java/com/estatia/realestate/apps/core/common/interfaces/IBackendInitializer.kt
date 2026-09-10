package com.estatia.realestate.apps.core.common.interfaces
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Interface for backend-specific initialization logic.
 */
@Contract
interface IBackendInitializer {
    /**
     * Called during application startup to initialize the backend SDKs or observability.
     */
    suspend fun initialize()
}
