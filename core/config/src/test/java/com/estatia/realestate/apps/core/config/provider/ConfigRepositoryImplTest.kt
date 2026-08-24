package com.estatia.realestate.apps.core.config.provider

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.config.datasource.AssetConfigDataSource
import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.domain.config.IConfigDataRepository
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.model.config.RemoteConfigModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfigRepositoryImplTest {

    private lateinit var assetSource: AssetConfigDataSource
    private lateinit var dataRepository: IConfigDataRepository
    private lateinit var parser: ConfigParser
    private lateinit var stateHolder: ConfigStateHolder
    private lateinit var metricsTracker: IMetricsTracker
    private lateinit var repository: ConfigProvider

    private val mockConfig = mockk<RemoteConfigModel>(relaxed = true) {
        every { network.baseUrl } returns "https://api.estatia.com"
        every { security.enableLogging } returns true
        every { network.cdnEndpoints } returns emptyList()
    }

    @Before
    fun setup() {
        assetSource = mockk()
        dataRepository = mockk()
        parser = mockk()
        stateHolder = mockk(relaxed = true)
        metricsTracker = mockk(relaxed = true)
        repository = ConfigProvider(assetSource, dataRepository, parser, stateHolder, metricsTracker)
    }

    @Test
    fun `initialize loads from assets and refreshes from remote successfully`() = runTest {
        val json = "{}"
        coEvery { assetSource.loadNetworkConfig() } returns json
        coEvery { assetSource.loadSecurityConfig() } returns json
        coEvery { assetSource.loadPlayerConfig() } returns json
        coEvery { assetSource.loadChaosConfig() } returns json
        
        coEvery { dataRepository.fetchRemoteConfig() } returns AppResult.Success(json)
        
        every { parser.parseNetwork(any()) } returns mockk(relaxed = true)
        every { parser.parseSecurity(any()) } returns mockk(relaxed = true)
        every { parser.parsePlayer(any()) } returns mockk(relaxed = true)
        every { parser.parseChaos(any()) } returns mockk(relaxed = true)
        every { parser.parse(any()) } returns mockConfig

        repository.initialize()

        assertTrue(repository.isInitialized)
        verify { stateHolder.update(mockConfig) }
        assertEquals("https://api.estatia.com", repository.baseUrl)
    }

    @Test
    fun `refresh handles transient network failures gracefully using platform models`() = runTest {
        // 🧪 Chaos: Remote fetch fails
        coEvery { dataRepository.fetchRemoteConfig() } returns AppResult.Error(NetworkException.Timeout)

        repository.refresh()

        // Verify metrics reported the failure
        verify { metricsTracker.incrementCounter("config_refresh_failed", any()) }
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing getters before initialization throws exception`() {
        repository.baseUrl
    }
}
