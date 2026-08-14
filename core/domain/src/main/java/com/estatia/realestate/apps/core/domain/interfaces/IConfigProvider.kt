package com.estatia.realestate.apps.core.domain.interfaces

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.api.ApiEndpoint
import com.estatia.realestate.apps.core.model.config.ChaosConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import kotlinx.coroutines.flow.StateFlow

interface IConfigProvider {

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

    val baseUrl: String

    val apiEndpoints: List<ApiEndpoint>

    val isLoggingEnabled: Boolean

    val isTelemetryEnabled: Boolean

    val googleKeyPattern: Regex

    val genericKeyPattern: Regex

    val paymentsKeyPattern: Regex

    val cdnEndpoints: List<CdnEndpoint>

    val encryptionLocationId: String

    val encryptionKeyRingId: String

    val symmetricKeyId: String

    val asymmetricKeyId: String

    val asymmetricSigningKeyId: String

    val chaosConfig: ChaosConfig

    val playerTuning: PlayerTuningConfig

    val configVersion: StateFlow<Long>
}
