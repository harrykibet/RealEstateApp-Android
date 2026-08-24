package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.feature.PaymentStatus
import com.estatia.realestate.apps.core.model.property.Money
import com.estatia.realestate.apps.core.network.interfaces.IPaymentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IPaymentsRemoteDataSource] using AWS Lambda (via AppSync).
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: High-integrity financial transaction triggers.
 * - Idempotency: Delegates idempotency logic to the backend Lambda; client should provide a unique Reference ID if supported by API.
 * - Concurrency: Thread-safe.
 * - Resilience: Transparently uses [networkClient] for retries.
 * - Observability: Tracks payment processing latency and terminal status.
 */
internal class AwsPaymentsRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient,
    private val metricsTracker: IMetricsTracker
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
        
        // Using a Map for the response to extract the status field from the nested JSON
        val request = SimpleGraphQLRequest<Map<*, *>>(
            mutation,
            mapOf("amount" to amount.amount, "currency" to currency, "method" to method.toString()),
            Map::class.java,
            null
        )

        val startTime = System.currentTimeMillis()

        return networkClient.execute {
            val result = suspendCancellableCoroutine { continuation ->
                Amplify.API.mutate(
                    request,
                    { response -> 
                        val data = response.data
                        val processPayment = data?.get("processPayment") as? Map<*, *>
                        val statusString = processPayment?.get("status") as? String
                        
                        val status = try {
                            PaymentStatus.valueOf(statusString?.uppercase() ?: "FAILED")
                        } catch (_: Exception) {
                            PaymentStatus.FAILED
                        }
                        continuation.resume(status) 
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }

            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.aws.payment_latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.aws.payment.${result.name.lowercase()}")

            result
        }
    }
}
