package com.estatia.realestate.apps.feature.search

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.ISearchRepository
import com.estatia.realestate.apps.core.domain.usecase.TogglePropertyLikeUseCase
import com.estatia.realestate.apps.core.testing.fake.analytics.FakeEngagementRepository
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
class SearchTrackingTest {

    private lateinit var searchRepository: ISearchRepository
    private lateinit var engagementRepository: FakeEngagementRepository
    private lateinit var togglePropertyLikeUseCase: TogglePropertyLikeUseCase
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        searchRepository = mockk(relaxed = true)
        engagementRepository = FakeEngagementRepository()
        togglePropertyLikeUseCase = mockk(relaxed = true)
        
        viewModel = SearchViewModel(
            searchRepository,
            engagementRepository,
            togglePropertyLikeUseCase,
            SavedStateHandle()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onResultClicked records search engagement in witness`() = runTest {
        val query = "Nairobi"
        val propertyId = "prop_123"
        
        coEvery { searchRepository.searchProperties(query, 20) } returns AppResult.Success(emptyList())
        viewModel.searchProperties(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onResultClicked(propertyId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Verify via Witness
        engagementRepository.witness.assertContains(
            FakeEngagementRepository.EngagementSignal.Search(query, propertyId)
        )
    }
}
