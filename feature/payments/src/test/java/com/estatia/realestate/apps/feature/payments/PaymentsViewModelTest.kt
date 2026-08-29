package com.estatia.realestate.apps.feature.payments

import androidx.lifecycle.SavedStateHandle
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.usecase.ProcessPaymentUseCase
import com.estatia.realestate.apps.core.model.feature.PaymentContext
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.navigation.routes.PaymentRoute
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.core.testing.assertions.assertState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.navigation.toRoute

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentsViewModelTest {

    private lateinit var processPaymentUseCase: ProcessPaymentUseCase
    private lateinit var viewModel: PaymentsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        processPaymentUseCase = mockk()
        
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        
        val savedStateHandle = mockk<SavedStateHandle>()
        val paymentRoute = PaymentRoute(
            referenceId = "ref_123",
            amount = 100.0,
            currency = "USD",
            context = PaymentContext.BOOKING
        )
        
        every { savedStateHandle.toRoute<PaymentRoute>() } returns paymentRoute
        
        viewModel = PaymentsViewModel(processPaymentUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
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

        viewModel.state.assertState { uiState is PaymentsUiState.Success }
    }
}
