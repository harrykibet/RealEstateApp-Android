package com.application.real_estate_app.core_network.sources

import com.application.real_estate_app.core_common.errors.Errors
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.application.real_estate_app.core_network.db_entities.PropertyEntity
import com.application.real_estate_app.core_network.db_names.FirestoreFields
import com.application.real_estate_app.core_network.mappers.toDomainModel
import com.application.real_estate_app.core_network.interfaces.INetworkHandler
import com.application.real_estate_app.core_model.Property
import com.application.real_estate_app.core_network.db_names.FirestoreCollections
import com.application.real_estate_app.core_network.interfaces.ISearchRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

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

    private fun log(message: String?) {
        logger.e("${Errors.SEARCH_REPO} : $message")
    }
}
