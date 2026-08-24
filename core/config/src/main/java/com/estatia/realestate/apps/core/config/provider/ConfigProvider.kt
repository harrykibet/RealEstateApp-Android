package com.estatia.realestate.apps.core.config.provider

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.config.datasource.AssetConfigDataSource
import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.domain.config.IConfigDataRepository
import com.estatia.realestate.apps.core.domain.config.IConfigProvider
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.api.ApiEndpoint
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.RemoteConfigModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ConfigProvider @Inject constructor(
    private val assetSource: AssetConfigDataSource,
    private val dataRepository: IConfigDataRepository,
    private val parser: ConfigParser,
    private val stateHolder: ConfigStateHolder,
    private val metricsTracker: IMetricsTracker
) : IConfigProvider {

    override val configVersion = stateHolder.configVersion

    private val _isReady = MutableStateFlow(false)
    override val isReady: StateFlow<Boolean> = _isReady

    override suspend fun awaitReady() {
        _isReady.first { it }
    }

    @Volatile
    private var cachedConfig: RemoteConfigModel? = null

    @Volatile
    private var cachedCdnEndpoints: List<CdnEndpoint> = emptyList()

    @Volatile
    private var googleRegex: Regex = Regex("^AIza[0-9A-Za-z_-]{35}$")

    @Volatile
    private var genericRegex: Regex = Regex("^[A-Za-z0-9]{32}$")

    @Volatile
    private var paymentsRegex: Regex = Regex("^[0-9A-Za-z]{40}$")

    override var isInitialized: Boolean = false
        private set

    private fun requireConfig(): RemoteConfigModel {
        return cachedConfig
            ?: throw IllegalStateException(
                "ConfigRepository accessed before initialization"
            )
    }

    override suspend fun initialize() {

        if (isInitialized) return

        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()

            // Load from individual fragments in assets
            val networkJson = assetSource.loadNetworkConfig()
            val securityJson = assetSource.loadSecurityConfig()
            val playerJson = assetSource.loadPlayerConfig()

            val combined = RemoteConfigModel(
                network = parser.parseNetwork(networkJson),
                security = parser.parseSecurity(securityJson),
                player = parser.parsePlayer(playerJson)
            )

            applyConfig(combined)

            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("config.initialize.duration", duration.milliseconds)

            isInitialized = true
            _isReady.value = true

            // Trigger remote refresh asynchronously to avoid blocking startup on network
            CoroutineScope(Dispatchers.IO).launch {
                refresh()
            }
        }
    }

    override suspend fun refresh() {

        val result = dataRepository.fetchRemoteConfig()
        
        if (result is AppResult.Success<*>) {
            val remoteJson = result.data as? String ?: return

            val parsedResult = runCatching {
                // For remote refresh, we still assume a single unified RemoteConfigModel
                // but we might need to handle the new structure if the backend changes.
                // For now, this parse() will expect the new RemoteConfigModel structure.
                parser.parse(remoteJson)
            }
            val parsed = parsedResult.getOrNull() ?: return

            applyConfig(parsed)
        }
    }

    private fun applyConfig(config: RemoteConfigModel) {

        cachedConfig = config
        googleRegex = Regex(config.security.keyPatterns.google)
        genericRegex = Regex(config.security.keyPatterns.generic)
        paymentsRegex = Regex(config.security.keyPatterns.payments)
        cachedCdnEndpoints = config.network.cdnEndpoints
        stateHolder.update(config)

    }

    // -------------------------
    // Derived getters
    // -------------------------

    override val baseUrl: String
        get() = requireConfig().network.baseUrl

    override val apiEndpoints: List<ApiEndpoint>
        get() = requireConfig().network.apiEndpoints

    override val isLoggingEnabled: Boolean
        get() = requireConfig().security.enableLogging

    override val isTelemetryEnabled: Boolean
        get() = requireConfig().security.enableTelemetry

    override val googleKeyPattern: Regex
        get() = googleRegex

    override val genericKeyPattern: Regex
        get() = genericRegex

    override val paymentsKeyPattern: Regex
        get() = paymentsRegex

    override val cdnEndpoints: List<CdnEndpoint>
        get() = cachedCdnEndpoints

    override val encryptionLocationId: String
        get() = requireConfig().security.encryptionKeys.locationId

    override val encryptionKeyRingId: String
        get() = requireConfig().security.encryptionKeys.keyRingId

    override val symmetricKeyId: String
        get() = requireConfig().security.encryptionKeys.symmetricKeyId

    override val asymmetricKeyId: String
        get() = requireConfig().security.encryptionKeys.asymmetricKeyId

    override val asymmetricSigningKeyId: String
        get() = requireConfig().security.encryptionKeys.asymmetricSigningKeyId

    override val playerTuning: PlayerTuningConfig
        get() = requireConfig().player
}
