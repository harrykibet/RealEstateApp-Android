package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [ICommentsRemoteDataSource] using AppSync Subscriptions.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage real-time comment streams and submissions via AWS.
 * - Concurrency: Thread-safe; uses [callbackFlow] for subscription management.
 * - Resilience: Surfaces [DatabaseException.Unknown] on subscription failures.
 * - Observability: Tracks submission latency and subscription events.
 */
internal class AwsCommentsRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient,
    private val metricsTracker: IMetricsTracker
) : ICommentsRemoteDataSource {

    override suspend fun submitComment(comment: CommentEntityModel): AppResult<Unit> {
        val mutation = $$"""
            mutation CreateComment($input: CreateCommentInput!) {
                createComment(input: $input) { id }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("input" to comment),
            String::class.java,
            null
        )

        val startTime = System.currentTimeMillis()

        return networkClient.execute {
            val result = suspendCancellableCoroutine { continuation ->
                Amplify.API.mutate(request,
                    { _ -> continuation.resume(Unit) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }

            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.aws.comment_submit_latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.aws.comment_submit_success")

            result
        }
    }

    override fun observeComments(propertyId: String): Flow<AppResult<List<CommentEntityModel>>> = callbackFlow {
        // AppSync Subscription for real-time comments
        val subscriptionQuery = $$"""
            subscription OnCreateComment($propertyId: String!) {
                onCreateComment(propertyId: $propertyId) {
                    id
                    propertyId
                    authorId
                    message
                    timeStamp
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<CommentEntityModel>(
            subscriptionQuery,
            mapOf("propertyId" to propertyId),
            CommentEntityModel::class.java,
            null
        )

        val operation = Amplify.API.subscribe(request,
            { /* onStart */ },
            { event -> 
                val newComment = event.data
                if (newComment != null) {
                    metricsTracker.incrementCounter("network.aws.comment_received")
                    trySend(AppResult.Success(listOf(newComment)))
                }
            },
            { error -> 
                metricsTracker.incrementCounter("network.aws.comment_subscription_error")
                trySend(AppResult.Error(DatabaseException.Unknown(error))) 
            },
            { /* onComplete */ }
        )

        awaitClose { operation?.cancel() }
    }
}
