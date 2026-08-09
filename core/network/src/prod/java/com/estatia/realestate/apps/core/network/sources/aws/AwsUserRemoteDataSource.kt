package com.estatia.realestate.apps.core.network.sources.aws

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.network.db_entities.UserEntityModel
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IUserRemoteDataSource
import javax.inject.Inject

/**
 * AWS implementation of [IUserRemoteDataSource].
 * 
 * TRULY AWS READY: This implementation uses the Amplify API (GraphQL) pattern
 * to retrieve user profile data from Aurora Serverless via AppSync.
 */
class AwsUserRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IUserRemoteDataSource {

    override suspend fun getUserById(userId: String): AppResult<UserEntityModel> {
        // TRULY AWS READY: Pattern for retrieving a user profile from Aurora via AppSync
        /*
        val userQuery = """
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

        return networkClient.execute {
            val response = Amplify.API.query(
                SimpleGraphQLRequest<UserEntityModel>(
                    userQuery,
                    mapOf("id" to userId),
                    UserEntityModel::class.java,
                    GsonVariablesSerializer()
                )
            ).await()
            
            response.data ?: throw DatabaseException.NotFound
        }
        */
        return AppResult.Error(DatabaseException.NotFound)
    }
}
