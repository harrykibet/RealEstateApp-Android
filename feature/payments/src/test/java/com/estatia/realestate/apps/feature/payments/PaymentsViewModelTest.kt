package com.estatia.realestate.apps.feature.payments

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.usecase.ProcessPaymentUseCase
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.core.testing.assertions.assertState
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
class PaymentsViewModelTest {

    private lateinit var processPaymentUseCase: ProcessPaymentUseCase
    private lateinit var viewModel: PaymentsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        processPaymentUseCase = mockk()
        
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
        viewModel.state.assertProperty("ref_123") { referenceId }
        viewModel.state.assertProperty("USD") { currency }
    }

    @Test
    fun `processPayment success updates uiState to Success`() = runTest {
        coEvery { 
            processPaymentUseCase(any(), any(), any(), any()) 
        } returns AppResult.Success(PaymentStatus.SUCCESS)

        viewModel.processPayment()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.assertState { uiState is PaymentsUiState.Success }
    }
}
