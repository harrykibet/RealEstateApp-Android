package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase implementation of [IPaymentsRemoteDataSource].
 * Triggers a Firebase Cloud Function.
 */
class FirebasePaymentsRemoteDataSource @Inject constructor(
    private val functions: FirebaseFunctions,
    private val networkClient: INetworkClient
) : IPaymentsRemoteDataSource {

    override suspend fun processPayment(
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus> {
        return networkClient.execute {
            val data = hashMapOf(
                "amount" to amount.amount,
                "currency" to currency,
                "method" to method.toString()
            )

            val result = functions
                .getHttpsCallable("processPayment")
                .call(data)
                .await()

            // Map result.data to PaymentStatus
            PaymentStatus.SUCCESS
        }
    }
}
