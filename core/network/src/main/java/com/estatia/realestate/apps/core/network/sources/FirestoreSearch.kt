package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.common.misc.Consts
import com.estatia.realestate.apps.core.network.db_entities.PropertyEntityModel
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.ISearchRemoteDataSource
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FirestoreSearch @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : ISearchRemoteDataSource {

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<PropertyEntityModel?> {
        return network.safeApiCallSuspend(
            apiCall = {
                val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                    .whereEqualTo(FirestoreFields.TITLE, query)
                    .limit(limit.toLong())
                    .get()
                    .await()

                propertiesSnapshot.documents.map { it.toObject(PropertyEntityModel::class.java) }
            },
            onFailure = { e ->
                log(e.message)
                onFailure(e)
            }
        ) ?: emptyList()
    }

    override suspend fun loadNearbyProperties(map: GoogleMap, userLat: Double, userLng: Double): Boolean {
        val nearbyDistanceThreshold = 10.0 // Distance in kilometers

        return network.safeApiCallSuspend(
            apiCall = {
                val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                    .get()
                    .await()

                var propertiesFound = false

                for (document in propertiesSnapshot.documents) {
                    val propertyLat = document.getDouble(FirestoreFields.LATITUDE) ?: 0.0
                    val propertyLng = document.getDouble(FirestoreFields.LONGITUDE) ?: 0.0
                    val propertyName = document.getString(FirestoreFields.TITLE) ?: Consts.PROPERTY

                    val distanceToProperty = calculateDistance(userLat, userLng, propertyLat, propertyLng)

                    if (distanceToProperty <= nearbyDistanceThreshold) {
                        propertiesFound = true
                        val propertyLocation = LatLng(propertyLat, propertyLng)
                        map.addMarker(MarkerOptions().position(propertyLocation).title(propertyName))
                    }
                }
                propertiesFound
            },
            onFailure = { e ->
                log("Error loading nearby properties: ${e.message}")
            }
        ) ?: false
    }


    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers
        val latDiff = Math.toRadians(lat2 - lat1)
        val lngDiff = Math.toRadians(lng2 - lng1)

        val a = sin(latDiff / 2) * sin(latDiff / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lngDiff / 2) * sin(lngDiff / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    private fun log(message: String?) {
        logger.e("${Errors.SEARCH_REPO} : $message")
    }
}
