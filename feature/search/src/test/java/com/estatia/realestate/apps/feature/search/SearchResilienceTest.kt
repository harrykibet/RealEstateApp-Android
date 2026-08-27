package com.estatia.realestate.apps.feature.search

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.data.mappers.remote.RemotePropertyMapper
import com.estatia.realestate.apps.core.data.repositories.SearchRepository
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkBehavior
import com.estatia.realestate.apps.core.testing.chaos.network.NetworkChaosController
import com.estatia.realestate.apps.core.testing.fake.analytics.FakeEngagementRepository
import com.estatia.realestate.apps.core.testing.fake.source.FakePropertyLocalDataSource
import com.estatia.realestate.apps.core.testing.fake.source.FakeSearchLocalDataSource
import com.estatia.realestate.apps.core.testing.fixtures.PropertyFixtures
import com.estatia.realestate.apps.core.testing_network.chaos.ChaosNetworkClient
import com.estatia.realestate.apps.core.testing_network.fake.source.FakeSearchRemoteDataSource
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResilienceTest {

    private lateinit var searchRepository: ISearchRepository
    private lateinit var remoteDataSource: FakeSearchRemoteDataSource
    private lateinit var searchLocalDataSource: FakeSearchLocalDataSource
    private lateinit var propertyLocalDataSource: FakePropertyLocalDataSource
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var togglePropertyLikeUseCase: TogglePropertyLikeUseCase
    private lateinit var networkChaos: NetworkChaosController
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        networkChaos = NetworkChaosController()
        val exceptionMapper = mockk<com.estatia.realestate.apps.core.network.interfaces.IExceptionMapper>()
        every { exceptionMapper.map(any()) } answers {
            val t = firstArg<Throwable>()
            if (t is java.net.SocketTimeoutException) NetworkException.Timeout
            else NetworkException.ConnectionFailed
        }
        val retryPolicy = com.estatia.realestate.apps.core.network.core.ExponentialRetryPolicy(exceptionMapper)
        
        val chaosNetworkClient = ChaosNetworkClient(
            networkChaos = networkChaos,
            exceptionMapper = exceptionMapper,
            retryPolicy = retryPolicy
        )

        remoteDataSource = FakeSearchRemoteDataSource()
        // Wrap fake to use the chaos client for driving failure semantics
        val searchRemoteSource = object : com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource by remoteDataSource {
            override suspend fun searchProperties(query: String, limit: Int): com.estatia.realestate.apps.core.common.exceptions.AppResult<List<com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel>> {
                return chaosNetworkClient.execute(com.estatia.realestate.apps.core.network.core.RetryConfigs.PROPERTY_FEED) {
                    val result = remoteDataSource.searchProperties(query, limit)
                    (result as com.estatia.realestate.apps.core.common.exceptions.AppResult.Success).data
                }
            }
        }

        searchLocalDataSource = FakeSearchLocalDataSource()
        propertyLocalDataSource = FakePropertyLocalDataSource()
        engagementRepository = FakeEngagementRepository()
        
        searchRepository = SearchRepository(
            remoteDataSource = searchRemoteSource,
            searchLocalDataSource = searchLocalDataSource,
            propertyLocalDataSource = propertyLocalDataSource,
            engagementRepository = engagementRepository,
            metricsTracker = mockk(relaxed = true),
            exceptionTranslator = mockk(relaxed = true)
        )
        
        togglePropertyLikeUseCase = mockk(relaxed = true)
        viewModel = SearchViewModel(searchRepository, engagementRepository, togglePropertyLikeUseCase, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search handles transient network failures with automatic success on retry`() = runTest {
        // Given: Scenario where network is offline initially (exceeding retry limit)
        val query = "Nairobi"
        networkChaos.script(
            NetworkBehavior.Offline,
            NetworkBehavior.Offline,
            NetworkBehavior.Offline
        )
        
        // When: Search is triggered
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Error state is shown (after all retries exhausted)
        viewModel.uiState.assertState { 
            this is SearchUiState.Error && this.message.contains("Connection failed") 
        }

        // Given: Network is restored and properties are available
        networkChaos.reset()
        val matchingProperties = listOf(
            PropertyFixtures.build(title = "Apartment in Nairobi"),
            PropertyFixtures.default().copy(description = "Luxury house in Nairobi heart")
        )
        matchingProperties.forEach { remoteDataSource.addProperty(RemotePropertyMapper.toEntity(it)) }

        // When: Retry happens (re-trigger search)
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Success state is shown with matching results
        viewModel.uiState.assertState { 
            this is SearchUiState.Success && this.results.size == 2 
        }
    }
}
