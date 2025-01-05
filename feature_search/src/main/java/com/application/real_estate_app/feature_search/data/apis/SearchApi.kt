package com.application.real_estate_app.feature_search.data.apis

import android.util.Log
import com.application.real_estate_app.core.data_utils.entities.PropertyEntity
import com.application.real_estate_app.core.data_utils.mappers.toDomainModel
import com.application.real_estate_app.core.data_utils.models.Property
import com.application.real_estate_app.feature_search.domain.interfaces.ISearchApi
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchApi @Inject constructor(
    private val db: FirebaseFirestore //Injected via DI
) : ISearchApi {
    override suspend fun searchProperties(
        query: String,
        limit: Int
    ): List<Property> {
        return try {
            val propertiesSnapshot = db.collection("properties")
                .whereEqualTo("title", query)
                .limit(limit.toLong())
                .get()
                .await()

            propertiesSnapshot.documents.map { it.toObject(PropertyEntity::class.java)!!.toDomainModel() }
        } catch (e: Exception) {
            Log.e("PropertyRepository", "Error searching properties: ${e.message}")
            emptyList()
        }
    }
}