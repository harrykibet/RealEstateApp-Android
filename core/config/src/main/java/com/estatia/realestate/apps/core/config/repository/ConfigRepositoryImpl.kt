package com.estatia.realestate.apps.core.config.repository

import com.estatia.realestate.apps.core.config.datasource.AssetConfigDataSource
import com.estatia.realestate.apps.core.config.datasource.FirebaseRemoteConfigDataSource
import com.estatia.realestate.apps.core.config.model.RemoteConfigModel
import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val assetSource: AssetConfigDataSource,
    private val remoteSource: FirebaseRemoteConfigDataSource,
    private val parser: ConfigParser,
    private val stateHolder: ConfigStateHolder
) : ConfigRepository {

    override val configVersion = stateHolder.configVersion

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

            val defaultJson = assetSource.loadDefaultConfig()

            val parsed = parser.parse(defaultJson)

            applyConfig(parsed)

            isInitialized = true
        }

        refresh()
    }

    override suspend fun refresh() {

        val remoteJson = remoteSource.fetch() ?: return

        val parsed = runCatching {
            parser.parse(remoteJson)
        }.getOrNull() ?: return

        applyConfig(parsed)
    }

    private fun applyConfig(config: RemoteConfigModel) {

        cachedConfig = config

        googleRegex = Regex(config.keyPatterns.google)
        genericRegex = Regex(config.keyPatterns.generic)
        paymentsRegex = Regex(config.keyPatterns.payments)

        cachedCdnEndpoints = buildCdnEndpoints(config.cdnEndpoints)

        stateHolder.update(config)
    }

    private fun buildCdnEndpoints(urls: List<String>): List<CdnEndpoint> {

        if (urls.isEmpty()) return emptyList()

        return urls.mapIndexed { index, url ->

            CdnEndpoint(
                name = "cdn-${index + 1}",
                baseUrl = url
            )
        }
    }

    // -------------------------
    // Derived getters
    // -------------------------

    override val baseUrl: String
        get() = requireConfig().baseConfig.baseUrl

    override val isLoggingEnabled: Boolean
        get() = requireConfig().baseConfig.enableLogging

    override val googleKeyPattern: Regex
        get() = googleRegex

    override val genericKeyPattern: Regex
        get() = genericRegex

    override val paymentsKeyPattern: Regex
        get() = paymentsRegex

    override val cdnEndpoints: List<CdnEndpoint>
        get() = cachedCdnEndpoints

    override val encryptionLocationId: String
        get() = requireConfig().encryptionKeys.locationId

    override val encryptionKeyRingId: String
        get() = requireConfig().encryptionKeys.keyRingId

    override val symmetricKeyId: String
        get() = requireConfig().encryptionKeys.symmetricKeyId

    override val asymmetricKeyId: String
        get() = requireConfig().encryptionKeys.asymmetricKeyId

    override val asymmetricSigningKeyId: String
        get() = requireConfig().encryptionKeys.asymmetricSigningKeyId
}