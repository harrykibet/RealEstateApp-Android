package com.estatia.realestate.apps.core.network.sources.aws

import android.content.Context
import android.net.Uri
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageUploadFileOptions
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [IPropertyRemoteDatasource].
 * Uses Amplify API (GraphQL) to interact with Aurora Serverless via AppSync,
 * and Amplify Storage for S3 uploads.
 */
internal class AwsPropertyRemoteDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkClient: INetworkClient,
    private val mediaCompressor: IMediaCompressor,
) : IPropertyRemoteDatasource {

    override suspend fun uploadProperty(
        property: PropertyEntityModel,
        contactInfo: PropertyContactEntity,
        imageUris: List<Uri>,
        videoUris: List<Uri>
    ): AppResult<String> = networkClient.execute {
        val propertyId = property.id.ifBlank { UUID.randomUUID().toString() }
        val outputDir = File(context.cacheDir, "uploads/$propertyId").apply { mkdirs() }
        
        coroutineScope {
            // 1. Compress and Upload Videos
            val videoUploads = videoUris.map { uri ->
                async {
                    val compressedFile = suspendCancellableCoroutine<File?> { continuation ->
                        mediaCompressor.compressVideo(context, uri, outputDir) { file ->
                            continuation.resume(file)
                        }
                    }
                    val fileToUpload = compressedFile ?: File(uri.path!!)
                    val key = "properties/$propertyId/videos/${UUID.randomUUID()}.mp4"
                    uploadFileToS3(key, fileToUpload)
                    key
                }
            }

            // 2. Compress and Upload Images
            val imageUploads = imageUris.map { uri ->
                async {
                    val compressedFile = mediaCompressor.compressImage(context, uri, outputDir)
                    val fileToUpload = compressedFile ?: File(uri.path!!)
                    val key = "properties/$propertyId/images/${UUID.randomUUID()}.jpg"
                    uploadFileToS3(key, fileToUpload)
                    key
                }
            }

            val videoKeys = videoUploads.awaitAll()
            val imageKeys = imageUploads.awaitAll()

            // 3. Create entry in Aurora via AppSync (GraphQL Mutation)
            val mutation = """
                mutation CreateProperty(${'$'}input: CreatePropertyInput!) {
                    createProperty(input: ${'$'}input) { id }
                }
            """.trimIndent()

            val request = SimpleGraphQLRequest<String>(
                mutation,
                mapOf(
                    "input" to property.copy(
                        id = propertyId, 
                        imageUrl = imageKeys,
                        directVideoUrls = videoKeys
                    )
                ),
                String::class.java,
                null
            )

            suspendCancellableCoroutine { continuation ->
                Amplify.API.mutate(
                    request,
                    { _ -> continuation.resume(propertyId) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }

    private suspend fun uploadFileToS3(key: String, file: File) = suspendCancellableCoroutine<Unit> { continuation ->
        val options = StorageUploadFileOptions.defaultInstance()
        @Suppress("DEPRECATION")
        Amplify.Storage.uploadFile(key, file, options,
            { _ -> continuation.resume(Unit) },
            { error -> continuation.resumeWith(Result.failure(error)) }
        )
    }

    override suspend fun updateProperty(propertyId: String, updates: Map<String, Any>): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation UpdateProperty(${'$'}id: ID!, ${'$'}updates: AWSJSON!) {
                updateProperty(id: ${'$'}id, updates: ${'$'}updates) { id }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("id" to propertyId, "updates" to updates),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun deleteProperty(propertyId: String): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation DeleteProperty(${'$'}id: ID!) {
                deleteProperty(id: ${'$'}id) { id }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("id" to propertyId),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
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

    override suspend fun fetchLikedProperties(userId: String): AppResult<List<PropertyEntityModel>> = networkClient.execute {
        val query = """
            query ListLikedProperties(${'$'}userId: ID!) {
                listLikedProperties(userId: ${'$'}userId) {
                    items { id, title, price, imageUrl }
                }
            }
        """.trimIndent()

        @Suppress("UNCHECKED_CAST")
        val request = SimpleGraphQLRequest<List<PropertyEntityModel>>(
            query,
            mapOf("userId" to userId),
            List::class.java as Class<List<PropertyEntityModel>>,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.query(request,
                { response -> continuation.resume(response.data ?: emptyList()) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun likeProperty(userId: String, propertyId: String): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation LikeProperty(${'$'}userId: ID!, ${'$'}propertyId: ID!) {
                likeProperty(userId: ${'$'}userId, propertyId: ${'$'}propertyId) { success }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("userId" to userId, "propertyId" to propertyId),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun unlikeProperty(userId: String, propertyId: String): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation UnlikeProperty(${'$'}userId: ID!, ${'$'}propertyId: ID!) {
                unlikeProperty(userId: ${'$'}userId, propertyId: ${'$'}propertyId) { success }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("userId" to userId, "propertyId" to propertyId),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun recordView(propertyId: String): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation RecordView(${'$'}propertyId: ID!) {
                recordView(propertyId: ${'$'}propertyId) { success }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("propertyId" to propertyId),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun recordShare(propertyId: String): AppResult<Unit> = networkClient.execute {
        val mutation = """
            mutation RecordShare(${'$'}propertyId: ID!) {
                recordShare(propertyId: ${'$'}propertyId) { success }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            mutation,
            mapOf("propertyId" to propertyId),
            String::class.java,
            null
        )

        suspendCancellableCoroutine { continuation ->
            Amplify.API.mutate(request,
                { _ -> continuation.resume(Unit) },
                { error -> continuation.resumeWith(Result.failure(error)) }
            )
        }
    }

    override suspend fun fetchPropertiesPaginated(
        userId: String?,
        cursor: PropertyCursor?,
        pageSize: Int
    ): AppResult<PropertyRemotePage> {
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
