package com.estatia.realestate.apps.core.domain.config

import kotlinx.coroutines.flow.StateFlow

interface IConfigLifecycle {
    /**
     * Emits true when the configuration has been loaded (at least from local assets).
     */
    val isReady: StateFlow<Boolean>

    /**
     * Suspends until the configuration is ready.
     */
    suspend fun awaitReady()

    val isInitialized: Boolean

    suspend fun initialize()

    suspend fun refresh()

    val configVersion: StateFlow<Long>
}
