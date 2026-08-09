package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money

/**
 * Interface for payment-related network operations, often handled via server-side logic (AWS Lambda / Cloud Functions).
 */
interface IPaymentsRemoteDataSource {
    /**
     * Processes a payment transaction.
     * 
     * @param amount The amount to charge.
     * @param currency The currency code (e.g., "USD").
     * @param method The payment method selected by the user.
     * @return The resulting [PaymentStatus] wraped in an [AppResult].
     */
    suspend fun processPayment(
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus>
}
