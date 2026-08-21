package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.interfaces.IPaymentsRepository
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import javax.inject.Inject

internal class PaymentsRepository @Inject constructor(
    private val remoteDataSource: IPaymentsRemoteDataSource
) : IPaymentsRepository {

    override suspend fun processPayment(
        referenceId: String,
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus> {
        // Here we can add idempotency key handling, local logging, etc.
        return remoteDataSource.processPayment(amount, currency, method)
    }
}
