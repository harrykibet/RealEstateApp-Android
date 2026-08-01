package com.estatia.realestate.apps.core.config.repository

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.config.datasource.AssetConfigDataSource
import com.estatia.realestate.apps.core.config.parser.ConfigParser
import com.estatia.realestate.apps.core.config.runtime.ConfigStateHolder
import com.estatia.realestate.apps.core.domain.interfaces.IConfigDataRepository
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
    private lateinit var repository: ConfigRepositoryImpl

    private val mockConfig = mockk<RemoteConfigModel>(relaxed = true) {
        every { keyPatterns.google } returns "google-regex"
        every { keyPatterns.generic } returns "generic-regex"
        every { keyPatterns.payments } returns "payments-regex"
        every { baseConfig.baseUrl } returns "https://api.estatia.com"
        every { baseConfig.enableLogging } returns true
        every { cdnEndpoints } returns emptyList()
    }

    @Before
    fun setup() {
        assetSource = mockk()
        dataRepository = mockk()
        parser = mockk()
        stateHolder = mockk(relaxed = true)
        repository = ConfigRepositoryImpl(assetSource, dataRepository, parser, stateHolder)
    }

    @Test
    fun `initialize loads from asset and refreshes from remote`() = runTest {
        val assetJson = "{asset}"
        val remoteJson = "{remote}"
        coEvery { assetSource.loadDefaultConfig() } returns assetJson
        coEvery { dataRepository.fetchRemoteConfig() } returns AppResult.Success(remoteJson)
        every { parser.parse(any()) } returns mockConfig

        repository.initialize()

        assertTrue(repository.isInitialized)
        verify { parser.parse(assetJson) }
        verify { stateHolder.update(mockConfig) }
        assertEquals("https://api.estatia.com", repository.baseUrl)
    }

    @Test
    fun `refresh updates state when remote data is available`() = runTest {
        val remoteJson = "{remote}"
        coEvery { dataRepository.fetchRemoteConfig() } returns AppResult.Success(remoteJson)
        every { parser.parse(remoteJson) } returns mockConfig

        repository.refresh()

        verify { stateHolder.update(mockConfig) }
    }

    @Test(expected = IllegalStateException::class)
    fun `accessing getters before initialization throws exception`() {
        repository.baseUrl
    }
}
