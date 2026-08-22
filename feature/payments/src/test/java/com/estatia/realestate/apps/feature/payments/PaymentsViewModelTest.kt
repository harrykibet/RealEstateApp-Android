package com.estatia.realestate.apps.feature.payments

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.usecase.ProcessPaymentUseCase
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.navigation.routes.PaymentRoute
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentsViewModelTest {

    private lateinit var processPaymentUseCase: ProcessPaymentUseCase
    private lateinit var viewModel: PaymentsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        processPaymentUseCase = mockk()
        
        // Mocking SavedStateHandle to return expected Route
        // Using a real SavedStateHandle with required arguments for toRoute()
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "referenceId" to "ref_123",
                "amount" to 100.0,
                "currency" to "USD"
            )
        )
        
        viewModel = PaymentsViewModel(processPaymentUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have args from savedStateHandle`() {
        val state = viewModel.state.value
        assertEquals("ref_123", state.referenceId)
        assertEquals(100.0, state.amount, 0.0)
        assertEquals("USD", state.currency)
    }

    @Test
    fun `processPayment success updates uiState to Success`() = runTest {
        coEvery { 
            processPaymentUseCase(any(), any(), any(), any()) 
        } returns AppResult.Success(PaymentStatus.SUCCESS)

        viewModel.processPayment()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value.uiState is PaymentsUiState.Success)
    }
}
