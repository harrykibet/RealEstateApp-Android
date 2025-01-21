package com.application.real_estate_app.feature_analytics.data.apis

import com.application.real_estate_app.core.data_utils.data_models.AnalyticsEvent
import com.application.real_estate_app.core.data_utils.db_names.FirestoreCollections
import com.application.real_estate_app.core.data_utils.db_names.FirestoreFields
import com.application.real_estate_app.core.interfaces.INetworkHandler
import com.application.real_estate_app.feature_analytics.domain.interfaces.IAnalyticsApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnalyticsApi @Inject constructor(
    db: FirebaseFirestore,
    private val network: INetworkHandler
) : IAnalyticsApi {

    private val analyticsCollection = db.collection(FirestoreCollections.ANALYTICS)

    override suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                // Firebase set operation
                analyticsCollection.document(event.eventId).set(event).await()
                true // Return true after successful operation
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        ) ?: false // Default to false if result is null
    }

    override suspend fun getEventsForUser(userId: String, onFailure: (Exception) -> Unit): List<AnalyticsEvent> {
        return network.safeApiCallSuspend(
            apiCall = {
                // Query Firebase for analytics events by userId
                val querySnapshot = analyticsCollection.whereEqualTo(FirestoreFields.USER_ID, userId).get().await()
                querySnapshot.documents.mapNotNull { it.toObject<AnalyticsEvent>() } // Convert to AnalyticsEvent
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        ) ?: emptyList() // Return empty list if something goes wrong
    }

    override suspend fun getEventById(eventId: String, onFailure: (Exception) -> Unit): AnalyticsEvent? {
        return network.safeApiCallSuspend(
            apiCall = {
                // Retrieve the event by ID
                val documentSnapshot = analyticsCollection.document(eventId).get().await()
                documentSnapshot.toObject<AnalyticsEvent>() // Convert Firestore document to AnalyticsEvent
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        ) // Return null if not found or error occurs
    }
}
