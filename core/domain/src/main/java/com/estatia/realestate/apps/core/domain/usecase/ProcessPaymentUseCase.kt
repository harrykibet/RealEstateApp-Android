package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.annotations.UseCase
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.IPaymentsRepository
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import javax.inject.Inject

@UseCase
class ProcessPaymentUseCase @Inject constructor(
    private val paymentsRepository: IPaymentsRepository
) {
    suspend operator fun invoke(
        referenceId: String,
        amount: Double,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus> {
        val money = Money(amount)
        return paymentsRepository.processPayment(referenceId, money, currency, method)
    }
}
