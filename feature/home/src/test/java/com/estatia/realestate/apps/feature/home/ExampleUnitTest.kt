package com.estatia.realestate.apps.feature.home

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.data.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyPage
import com.estatia.realestate.apps.feature.home.ui.viewModels.HomeViewModel
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
class HomeViewModelTest {

    private lateinit var propertyRepository: IPropertyRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        propertyRepository = mockk()
        viewModel = HomeViewModel(propertyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPropertiesSuccessShouldUpdateUiState() = runTest {
        // Given
        val properties = emptyList<com.estatia.realestate.apps.core.model.property.PropertyDomainModel>()
        val page = PropertyPage(properties, null)
        coEvery { propertyRepository.fetchPropertiesPaginated(null, 20) } returns AppResult.Success(page)

        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertEquals(false, initialState.isLoading)

            // When
            viewModel.fetchProperties(isFirstLoad = true, pageSize = 20)

            // Then
            assertEquals(true, awaitItem().isLoading)
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals(properties, finalState.properties)
        }
    }

    @Test
    fun fetchPropertiesFailureShouldUpdateErrorState() = runTest {
        // Given
        val exception = com.estatia.realestate.apps.core.common.exceptions.RemoteServiceException.Unknown(Exception("API Error"))
        coEvery { propertyRepository.fetchPropertiesPaginated(null, 20) } returns AppResult.Error(exception)

        viewModel.uiState.test {
            awaitItem() // Initial

            // When
            viewModel.fetchProperties(isFirstLoad = true, pageSize = 20)

            // Then
            assertEquals(true, awaitItem().isLoading)
            val finalState = awaitItem()
            assertEquals(false, finalState.isLoading)
            assertEquals("Failed to fetch properties. Please try again.", finalState.error)
        }
    }
}
