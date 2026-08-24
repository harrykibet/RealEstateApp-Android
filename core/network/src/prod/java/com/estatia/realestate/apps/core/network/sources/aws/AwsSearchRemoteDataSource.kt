package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * AWS implementation of [ISearchRemoteDataSource] using OpenSearch via AppSync.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Perform full-text and geospatial searches via AWS infrastructure.
 * - Concurrency: Thread-safe.
 * - Resilience: Delegates execution and retries to [networkClient].
 * - Observability: Tracks search latency and result count.
 */
internal class AwsSearchRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient,
    private val metricsTracker: IMetricsTracker
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

        val startTime = System.currentTimeMillis()

        return networkClient.execute {
            val result = suspendCancellableCoroutine { continuation ->
                Amplify.API.query(request,
                    { response -> continuation.resume(response.data ?: emptyList()) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }
            
            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.aws.search_latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.aws.search_success")
            
            result
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
        
        val startTime = System.currentTimeMillis()

        return networkClient.execute {
            val result = suspendCancellableCoroutine { continuation ->
                Amplify.API.query(request,
                    { response -> continuation.resume(response.data ?: emptyList()) },
                    { error -> continuation.resumeWith(Result.failure(error)) }
                )
            }

            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.aws.nearby_search_latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.aws.nearby_search_success")

            result
        }
    }
}
