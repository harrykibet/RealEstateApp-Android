package com.estatia.realestate.apps.core.domain.repository

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money

interface IPaymentsRepository {
    suspend fun processPayment(
        referenceId: String,
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus>
}
