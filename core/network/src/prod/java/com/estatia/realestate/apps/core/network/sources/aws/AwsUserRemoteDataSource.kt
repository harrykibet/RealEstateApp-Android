package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IUserRemoteDataSource].
 * 
 * TRULY AWS READY: This implementation uses the Amplify API (GraphQL) pattern
 * to retrieve user profile data from Aurora Serverless via AppSync.
 */
internal class AwsUserRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IUserRemoteDataSource {

    override suspend fun getUserById(userId: String): AppResult<UserEntityModel> {
        val userQuery = $$"""
            query GetUser($id: ID!) {
                getUser(id: $id) {
                    userId
                    name
                    email
                    phoneNumber
                    profilePictureUrl
                    userType
                    verified
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<UserEntityModel>(
            userQuery,
            mapOf("id" to userId),
            UserEntityModel::class.java,
            null
        )

        return networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.API.query(request,
                    { response -> 
                        val data = response.data
                        if (data != null) continuation.resume(data)
                        else continuation.resumeWith(Result.failure(DatabaseException.NotFound))
                    },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }
}
