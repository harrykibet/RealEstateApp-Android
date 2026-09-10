package com.estatia.realestate.apps.feature.payments

import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.UiState

sealed interface PaymentsUiState {
@Helper
    data object Idle : PaymentsUiState
    data object Processing : PaymentsUiState
@Helper
    data class Success(val transactionId: String) : PaymentsUiState
    data class Error(val message: String) : PaymentsUiState
}

@UiState
data class PaymentsScreenState(
    val referenceId: String,
    val amount: Double,
    val currency: String,
    val selectedMethod: PaymentMethod = PaymentMethod.CreditCard,
    val uiState: PaymentsUiState = PaymentsUiState.Idle
)
