package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [ISearchRemoteDataSource].
 * 
 * TRULY AWS READY: This implementation uses the Amplify API (GraphQL) pattern
 * to interact with AWS OpenSearch via an AppSync bridge.
 */
internal class AwsSearchRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : ISearchRemoteDataSource {

    override suspend fun searchProperties(query: String, limit: Int): AppResult<List<PropertyEntityModel>> {
        val searchQuery = $$"""
            query SearchProperties($query: String!, $limit: Int!) {
                searchProperties(filter: { 
                    or: [
                        { title: { match: $query, fuzziness: "AUTO" } },
                        { description: { match: $query, fuzziness: "AUTO" } }
                    ]
                }, limit: $limit) {
                    items {
                        id
                        title
                        description
                        price
                    }
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<List<PropertyEntityModel>>(
            searchQuery,
            mapOf("query" to query, "limit" to limit),
            List::class.java as Class<List<PropertyEntityModel>>,
            null
        )

        return networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.API.query(request,
                    { response -> continuation.resume(response.data ?: emptyList()) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }

    override suspend fun getNearbyProperties(latitude: Double, longitude: Double, radiusKm: Double): AppResult<List<PropertyEntityModel>> {
        val geoQuery = $$"""
            query NearbyProperties($lat: Float!, $lng: Float!, $radius: String!) {
                searchProperties(filter: {
                    location: {
                        within: {
                            distance: $radius,
                            center: { lat: $lat, lon: $lng }
                        }
                    }
                }) {
                    items { id, title, latitude, longitude }
                }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<List<PropertyEntityModel>>(
            geoQuery,
            mapOf("lat" to latitude, "lng" to longitude, "radius" to "${radiusKm}km"),
            List::class.java as Class<List<PropertyEntityModel>>,
            null
        )
        
        return networkClient.execute {
            suspendCancellableCoroutine { continuation ->
                Amplify.API.query(request,
                    { response -> continuation.resume(response.data ?: emptyList()) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
        }
    }
}
