package com.estatia.realestate.apps.feature.market

import com.estatia.realestate.apps.core.testing.assertions.assertCurrentState
import kotlinx.coroutines.test.runTest
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
        viewModel.uiState.assertCurrentState {
            val current = this
            current is MarketUiState.Success && 
                current.featuredServices.size == 2 && 
                current.popularProducts.size == 1
        }
    }
}
