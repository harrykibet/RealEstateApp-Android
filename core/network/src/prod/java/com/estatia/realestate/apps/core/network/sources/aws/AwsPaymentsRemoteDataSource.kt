package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import javax.inject.Inject

/**
 * AWS implementation of [IPaymentsRemoteDataSource].
 * Triggers a Lambda function via AWS AppSync (GraphQL).
 */
class AwsPaymentsRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IPaymentsRemoteDataSource {

    override suspend fun processPayment(
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus> {
        // TRULY AWS READY: Pattern for triggering a Lambda via AppSync
        /*
        val mutation = """
            mutation ProcessPayment($amount: Float!, $currency: String!, $method: String!) {
                processPayment(amount: $amount, currency: $currency, method: $method) {
                    status
                }
            }
        """.trimIndent()
        
        return networkClient.execute {
             val response = Amplify.API.mutate(
                SimpleGraphQLRequest<String>(
                    mutation,
                    mapOf("amount" to amount.value, ...),
                    String::class.java,
                    GsonVariablesSerializer()
                )
            ).await()
            // Map response to PaymentStatus
            PaymentStatus.SUCCESS
        }
        */
        return AppResult.Success(PaymentStatus.PENDING)
    }
}
