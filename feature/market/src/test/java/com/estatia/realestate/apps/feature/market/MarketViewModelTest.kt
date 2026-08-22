package com.estatia.realestate.apps.feature.market

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MarketViewModelTest {

    private lateinit var viewModel: MarketViewModel

    @Before
    fun setup() {
        viewModel = MarketViewModel()
    }

    @Test
    fun `initial state is Success with mock data`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state is MarketUiState.Success)
        val successState = state as MarketUiState.Success
        assertEquals(2, successState.featuredServices.size)
        assertEquals(1, successState.popularProducts.size)
        assertEquals(1, successState.recommendedProfessionals.size)
    }
}
