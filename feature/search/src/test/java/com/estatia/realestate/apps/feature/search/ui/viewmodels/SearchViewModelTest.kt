package com.estatia.realestate.apps.feature.search.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.generators.SearchQueryGenerator
import com.estatia.realestate.apps.feature.search.ui.SearchUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var searchRepository: ISearchRepository
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var togglePropertyLikeUseCase: TogglePropertyLikeUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchRepository = mockk()
        engagementRepository = mockk(relaxed = true)
        togglePropertyLikeUseCase = mockk()
        savedStateHandle = SavedStateHandle()
        
        coEvery { searchRepository.getSearchHistory() } returns AppResult.Success(emptyList())
        
        viewModel = SearchViewModel(searchRepository, engagementRepository, togglePropertyLikeUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSearchHistory success updates state to History`() = runTest {
        val history = listOf("Nairobi", "Apartment")
        coEvery { searchRepository.getSearchHistory() } returns AppResult.Success(history)

        viewModel.loadSearchHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertState {
            this is SearchUiState.History && this.history == history
        }
    }

    @Test
    fun `searchProperties success updates state to Success`() = runTest {
        val query = "Mombasa"
        coEvery { searchRepository.searchProperties(query, 20) } returns AppResult.Success(emptyList())

        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertState {
            this is SearchUiState.Success && this.query == query
        }
    }

    @Test
    fun `searchProperties failure updates state to Error`() = runTest {
        val query = "ErrorQuery"
        coEvery { searchRepository.searchProperties(query, 20) } returns AppResult.Error(
            DatabaseException.Unknown(Exception("Internal error"))
        )

        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertState {
            this is SearchUiState.Error && message.contains("database")
        }
    }

    @Test
    fun `onResultClicked reports engagement`() = runTest {
        val query = "Nairobi"
        val propertyId = "prop_1"
        val mockProperties = listOf(mockk<com.estatia.realestate.apps.core.model.property.PropertyDomainModel>())
        coEvery { searchRepository.searchProperties(query, 20) } returns AppResult.Success(mockProperties)

        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onResultClicked(propertyId)
        testDispatcher.scheduler.advanceUntilIdle()

        io.mockk.coVerify { engagementRepository.reportSearch(query, propertyId) }
    }

    @Test
    fun `viewModel handles random generated queries safely`() = runTest {
        val query = SearchQueryGenerator.generate()
        coEvery { searchRepository.searchProperties(query, 20) } returns AppResult.Success(emptyList())
        
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.uiState.assertState { this is SearchUiState.Success && this.query == query }
    }
}
