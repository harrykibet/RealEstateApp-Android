package com.estatia.realestate.apps.core.network.sources.aws

import android.content.Context
import android.net.Uri
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.DatabaseException
import com.estatia.realestate.apps.core.common.interfaces.IMediaCompressor
import com.estatia.realestate.apps.core.model.property.PropertyCursor
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_entities.PropertyContactEntity
import com.estatia.realestate.apps.core.network.db_entities.PropertyRemotePage
import com.estatia.realestate.apps.core.network.interfaces.IPropertyRemoteDatasource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IPropertyRemoteDatasource].
 * Uses Amplify API (GraphQL) to interact with Aurora Serverless via AppSync.
 */
internal class AwsPropertyRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkClient: INetworkClient,
    private val mediaCompressor: IMediaCompressor
) : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        contactInfo: PropertyContactEntity,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> = networkClient.execute {
        val propertyId = property.id.ifBlank { UUID.randomUUID().toString() }
        val outputDir = File(context.cacheDir, "uploads/$propertyId")
        
        // 1. Compress and Upload Videos
        val videoKeys = videoUris.map { uri ->
            val compressedFile = suspendCancellableCoroutine<File?> { continuation ->
                mediaCompressor.compressVideo(context, uri, outputDir) { file ->
                    continuation.resume(file)
                }
            }
            
            val finalUri = compressedFile?.let { Uri.fromFile(it) } ?: uri
            val key = "properties/$propertyId/videos/${UUID.randomUUID()}.mp4"
            // Future: Amplify.Storage.uploadFile(key, finalUri).await()
            key
        }

        // 2. Compress and Upload Images
        val imageKeys = imageUris.map { uri ->
            val compressedFile = mediaCompressor.compressImage(context, uri, outputDir)
            val finalUri = compressedFile?.let { Uri.fromFile(it) } ?: uri
            val key = "properties/$propertyId/images/${UUID.randomUUID()}.jpg"
            // Future: Amplify.Storage.uploadFile(key, finalUri).await()
            key
        }

        // 3. Create entry in Aurora via AppSync (GraphQL Mutation)
        val mutation = """
            mutation CreateProperty(${'$'}input: CreatePropertyInput!) {
                createProperty(input: ${'$'}input) { id }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("input" to property.copy(
                id = propertyId, 
                imageUrl = imageKeys,
                directVideoUrls = videoKeys
            )),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { response -> continuation.resume(propertyId) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): AppResult<Unit> {
        return AppResult.Error(DatabaseException.Unknown(Exception("AWS Update Not Implemented")))
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> {
        return AppResult.Error(DatabaseException.Unknown(Exception("AWS Delete Not Implemented")))
    }

    override suspend fun getPropertyById(propertyId: String): AppResult<PropertyEntityModel> {
        val query = """
            query GetProperty(${'$'}id: ID!) {
                getProperty(id: ${'$'}id) {
                    id
                    title
                    description
                    price
                    imageUrl
                    directVideoUrls
                    hlsUrl
                    ownerId
                    ownerName
                    latitude
                    longitude
                    createdAt
                    county
                    active
                    viewsCount
                    sharesCount
                    likesCount
                    commentsCount
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<PropertyEntityModel>(
            query,
            mapOf("id" to propertyId),
            PropertyEntityModel::class.java,
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

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> =
        AppResult.Success(emptyList())

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun recordView(propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun recordShare(propertyId: String): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {
        // 🏎️ Server-Side Delegation:
        // We invoke the 'getPersonalizedFeed' query which triggers our AppSync Lambda Resolver.
        // The Lambda handles fetching from Aurora, computing match scores, and ranking.
        val query = """
            query GetPersonalizedFeed(${'$'}userId: ID, ${'$'}limit: Int, ${'$'}nextToken: String) {
                getPersonalizedFeed(userId: ${'$'}userId, limit: ${'$'}limit, nextToken: ${'$'}nextToken) {
                    items {
                        id
                        title
                        description
                        price
                        imageUrl
                        directVideoUrls
                        hlsUrl
                        video
                        ownerId
                        ownerName
                        matchScore
                    }
                    nextToken
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<PropertyRemotePage>(
            query,
            mapOf(
                "userId" to userId,
                "limit" to pageSize,
                "nextToken" to cursor?.documentId
            ),
            PropertyRemotePage::class.java,
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
