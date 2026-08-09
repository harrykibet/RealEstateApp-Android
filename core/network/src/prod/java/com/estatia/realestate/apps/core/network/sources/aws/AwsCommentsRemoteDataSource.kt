package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.CommentEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ICommentsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [ICommentsRemoteDataSource].
 * 
 * TRULY AWS READY: This implementation pattern uses the Amplify API (GraphQL)
 * for real-time comments using AppSync Subscriptions.
 */
class AwsCommentsRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : ICommentsRemoteDataSource {

    override suspend fun submitComment(comment: CommentEntityModel): AppResult<Unit> {
        val mutation = """
            mutation CreateComment(${'$'}input: CreateCommentInput!) {
                createComment(input: ${'$'}input) { id }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("input" to comment),
            String::class.java,
            null
        )

        return networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.API.mutate(request,
                    { response -> continuation.resume(Unit) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }

    override fun observeComments(propertyId: String): Flow<AppResult<List<CommentEntityModel>>> = callbackFlow {
        // AppSync Subscription for real-time comments
        val subscriptionQuery = """
            subscription OnCreateComment(${'$'}propertyId: String!) {
                onCreateComment(propertyId: ${'$'}propertyId) {
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
                    trySend(AppResult.Success(listOf(newComment)))
                }
            },
            { error -> trySend(AppResult.Error(DatabaseException.Unknown(error))) },
            { /* onComplete */ }
        )

        awaitClose { operation?.cancel() }
    }
}
