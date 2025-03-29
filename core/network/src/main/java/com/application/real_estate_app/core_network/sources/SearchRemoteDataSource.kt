package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.errors.Errors
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_common.misc.Consts
import com.application.real_estate_app.core_network.db_entities.PropertyEntity
import com.application.real_estate_app.core_network.db_names.FirestoreFields
import com.application.real_estate_app.core_network.mappers.toDomainModel
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_model.Property
import com.google.android.gms.maps.model.MarkerOptions
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.application.real_estate_app.core_network.interfaces.ISearchRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SearchRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val logger: LoggerInterface, // Injected via DI
    private val network: INetworkHandler // Injected via DI
) : ISearchRemoteDataSource {

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property> {
        return network.safeApiCallSuspend(
            apiCall = {
                val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                    .whereEqualTo(FirestoreFields.TITLE, query)
                    .limit(limit.toLong())
                    .get()
                    .await()

                propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
            },
            onFailure = { e ->
                log(e.message)
                onFailure(e)
            }
        ) ?: emptyList()
    }

    override suspend fun loadNearbyProperties(userLat: Double, userLng: Double) : Boolean {
        val nearbyDistanceThreshold = 10.0 // Distance in kilometers
        var propertiesFound = false
        //TODO("Create a property repository method for the below logic")

        db.collection(FirestoreCollections.PROPERTIES)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        for (document in it.documents) {
                            val propertyLat = document.getDouble(FirestoreFields.LATITUDE) ?: 0.0
                            val propertyLng = document.getDouble(FirestoreFields.LONGITUDE) ?: 0.0
                            val propertyName = document.getString(FirestoreFields.TITLE) ?: Consts.PROPERTY

                            val distanceToProperty =
                                calculateDistance(userLat, userLng, propertyLat, propertyLng)

                            if (distanceToProperty <= nearbyDistanceThreshold) {
                                propertiesFound = true
                                val propertyLocation = LatLng(propertyLat, propertyLng)
                                map.addMarker(
                                    MarkerOptions().position(propertyLocation).title(propertyName)
                                )
                            }
                        }
                    }
                }
            }.await()
        return propertiesFound
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
