package com.estatia.realestate.apps.feature.search

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.fixtures.PropertyFixtures
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import com.estatia.realestate.apps.feature.search.ui.viewmodels.SearchViewModel
import io.mockk.coEvery
import io.mockk.mockk
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
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var togglePropertyLikeUseCase: TogglePropertyLikeUseCase
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchRepository = mockk(relaxed = true)
        engagementRepository = mockk(relaxed = true)
        togglePropertyLikeUseCase = mockk(relaxed = true)
        viewModel = SearchViewModel(searchRepository, engagementRepository, togglePropertyLikeUseCase, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search handles transient network failures with automatic success on retry`() = runTest {
        // Given: Scenario where network is offline initially
        val query = "Nairobi"
        coEvery { searchRepository.searchProperties(query, 20) } returns 
            AppResult.Error(NetworkException.NoInternet)
        
        // When: Search is triggered
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Error state is shown
        viewModel.uiState.assertState { 
            this is SearchUiState.Error && message.contains("internet") 
        }

        // Given: Network is restored
        coEvery { searchRepository.searchProperties(query, 20) } returns 
            AppResult.Success(PropertyFixtures.list(2))

        // When: Retry happens (re-trigger search)
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Success state is shown
        viewModel.uiState.assertState { 
            this is SearchUiState.Success && results.size == 2 
        }
    }
}
