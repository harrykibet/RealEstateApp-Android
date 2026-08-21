package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IPaymentsRemoteDataSource].
 * Triggers a Lambda function via AWS AppSync (GraphQL).
 */
internal class AwsPaymentsRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IPaymentsRemoteDataSource {

    override suspend fun processPayment(
        amount: Money,
        currency: String,
        method: PaymentMethod
    ): AppResult<PaymentStatus> {

        val mutation = $$"""
            mutation ProcessPayment($amount: Float!, $currency: String!, $method: String!) {
                processPayment(amount: $amount, currency: $currency, method: $method) {
                    status
                }
            }
        """.trimIndent()
        
        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("amount" to amount.amount, "currency" to currency, "method" to method.toString()),
            String::class.java,
            null
        )

        return networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.API.mutate(request,
                    { response -> 
                        val status = try {
                            // Extract status from GraphQL response JSON string
                            // Assuming response.data is something like {"processPayment": {"status": "SUCCESS"}}
                            PaymentStatus.valueOf(response.data.uppercase())
                        } catch (e: Exception) {
                            PaymentStatus.FAILED
                        }
                        continuation.resume(status) 
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }
}
