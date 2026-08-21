package com.estatia.realestate.apps.feature.payments

import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus

sealed interface PaymentsUiState {
    data object Idle : PaymentsUiState
    data object Processing : PaymentsUiState
    data class Success(val transactionId: String) : PaymentsUiState
    data class Error(val message: String) : PaymentsUiState
}

data class PaymentsScreenState(
    val referenceId: String,
    val amount: Double,
    val currency: String,
    val selectedMethod: PaymentMethod = PaymentMethod.CreditCard,
    val uiState: PaymentsUiState = PaymentsUiState.Idle
)
