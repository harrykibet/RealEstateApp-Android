package com.application.real_estate_app.feature_search.data.apis

import android.net.ConnectivityManager
import android.util.Log
import com.application.real_estate_app.core.data_utils.db_entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.data_models.Property
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.network_utils.NetworkHandler.safeApiCallSuspend
import com.application.real_estate_app.feature_search.domain.interfaces.ISearchApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchApi @Inject constructor(
    private val db: FirebaseFirestore, // Injected via DI
    private val connectivityManager: ConnectivityManager // Injected via DI
) : ISearchApi {

    override suspend fun searchProperties(
        query: String,
        limit: Int,
        onFailure: (Exception) -> Unit
    ): List<Property> {
        return safeApiCallSuspend(
            connectivityManager = connectivityManager,
            action = {
                val propertiesSnapshot = db.collection(FirestoreCollections.PROPERTIES)
                    .whereEqualTo("title", query)
                    .limit(limit.toLong())
                    .get()
                    .await()

                propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
            },
            onFailure = { e ->
                Log.e("SearchApi", "Error searching properties: ${e.message}")
                onFailure(e)
            }
        ) ?: emptyList()
    }
}
