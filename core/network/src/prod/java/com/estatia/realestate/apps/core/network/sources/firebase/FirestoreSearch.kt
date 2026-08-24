package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * Firebase implementation of [ISearchRemoteDataSource].
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Perform remote searches and geospatial queries via Firestore.
 * - Performance: 
 *   - Basic Search: O(log N) via Firestore ordered indexes.
 *   - Nearby Search: Currently O(N) due to client-side filtering; target geospatial indexing for future.
 * - Concurrency: Thread-safe.
 * - Resilience: Delegates execution and error mapping to [networkClient].
 * - Observability: Tracks search latency and result count for SLIs.
 */
internal class FirestoreSearch @Inject constructor(
    private val database: FirebaseFirestore,
    private val networkClient: INetworkClient,
    private val metricsTracker: IMetricsTracker
) : ISearchRemoteDataSource {


    override suspend fun searchProperties(
        query:String,
        limit:Int
    ): AppResult<List<PropertyEntityModel>> {

        val startTime = System.currentTimeMillis()

        return networkClient.execute {


            val results = database.collection(
                FirestoreCollections.PROPERTIES
            )
                .orderBy(
                    FirestoreFields.TITLE,
                    Query.Direction.ASCENDING
                )
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(limit.toLong())
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(
                        PropertyEntityModel::class.java
                    )
                }
            
            val duration = System.currentTimeMillis() - startTime
            metricsTracker.trackDuration("network.search.latency", duration.milliseconds)
            metricsTracker.incrementCounter("network.search.success")

            results
        }
    }


    /**
     * Retrieves properties within a given geographic radius.
     *
     * NOTE:
     * The current implementation performs a full collection scan and calculates
     * distances client-side. This approach does not scale efficiently as the
     * properties collection grows because it downloads all documents before
     * filtering results.
     *
     * FUTURE OPTIMIZATION:
     * Replace this approach with a geospatial query strategy:
     *
     * - Store a geohash field alongside latitude/longitude when creating properties.
     * - Query properties using geohash ranges based on the user's location.
     * - Optionally use Firestore geospatial libraries (e.g., GeoFirestore)
     *   or implement custom geohash indexing.
     *
     * This keeps filtering server-side, reduces bandwidth usage, lowers Firestore
     * read costs, and provides predictable performance for large datasets.
     */
    override suspend fun getNearbyProperties(
        latitude:Double,
        longitude:Double,
        radiusKm:Double
    ): AppResult<List<PropertyEntityModel>> {


        return networkClient.execute {


            val snapshot =
                database.collection(
                    FirestoreCollections.PROPERTIES
                )
                    .get()
                    .await()



            snapshot.documents
                .mapNotNull {

                    val property =
                        it.toObject(
                            PropertyEntityModel::class.java
                        )


                    property?.let { model ->

                        val distance =
                            calculateDistance(
                                latitude,
                                longitude,
                                model.latitude ?: return@let null,
                                model.longitude ?: return@let null
                            )


                        if(distance <= radiusKm)
                            model
                        else
                            null
                    }
                }
        }
    }



    private fun calculateDistance(
        lat1:Double,
        lng1:Double,
        lat2:Double,
        lng2:Double
    ):Double {


        val earthRadius = 6371.0


        val latDiff =
            Math.toRadians(
                lat2 - lat1
            )


        val lngDiff =
            Math.toRadians(
                lng2 - lng1
            )


        val a =
            sin(latDiff / 2) *
                    sin(latDiff / 2) +
                    cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(lngDiff / 2) *
                    sin(lngDiff / 2)


        val c =
            2 *
                    atan2(
                        sqrt(a),
                        sqrt(1 - a)
                    )


        return earthRadius * c
    }
}
