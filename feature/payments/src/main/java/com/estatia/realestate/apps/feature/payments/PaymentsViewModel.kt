package com.estatia.realestate.apps.feature.payments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.usecase.ProcessPaymentUseCase
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentResult
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.navigation.routes.PaymentRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val paymentArgs = savedStateHandle.toRoute<PaymentRoute>()

    private val _state = MutableStateFlow(
        PaymentsScreenState(
            referenceId = paymentArgs.referenceId,
            amount = paymentArgs.amount,
            currency = paymentArgs.currency
        )
    )
    val state: StateFlow<PaymentsScreenState> = _state.asStateFlow()

    fun onMethodSelected(method: PaymentMethod) {
        _state.update { it.copy(selectedMethod = method) }
    }

    fun processPayment() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = PaymentsUiState.Processing) }
            
            val result = processPaymentUseCase(
                referenceId = _state.value.referenceId,
                amount = _state.value.amount,
                currency = _state.value.currency,
                method = _state.value.selectedMethod
            )

            when (result) {
                is AppResult.Success -> {
                    if (result.data == PaymentStatus.SUCCESS) {
                        _state.update { it.copy(uiState = PaymentsUiState.Success(UUID.randomUUID().toString())) }
                    } else {
                        _state.update { it.copy(uiState = PaymentsUiState.Error("Payment failed with status: ${result.data}")) }
                    }
                }
                is AppResult.Error -> {
                    _state.update { it.copy(uiState = PaymentsUiState.Error(result.exception.message ?: "Unknown error")) }
                }
            }
        }
    }
}
