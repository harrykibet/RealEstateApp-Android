package com.application.real_estate_app.feature_analytics.data.repositories

import com.application.real_estate_app.core.domain.models.AnalyticsEvent
import com.application.real_estate_app.core.data.db_names.FirestoreCollections
import com.application.real_estate_app.core.data.db_names.FirestoreFields
import com.application.real_estate_app.core.common.errors.ErrorMessages
import com.application.real_estate_app.core.domain.interfaces.INetworkHandler
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.feature_analytics.domain.interfaces.IAnalyticsRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnalyticsRepository @Inject constructor(
    db: FirebaseFirestore,
    private val network: INetworkHandler,
    private val logger: LoggerInterface
) : IAnalyticsRepo {

    private val analyticsCollection = db.collection(FirestoreCollections.ANALYTICS)

    override suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                // Save the event to Firestore
                analyticsCollection.document(event.eventId).set(event).await()
                true // Indicate success
            },
            onFailure = { exception ->
                onFailure(exception)
                exception.message?.let { log(it) }
            }
        ) ?: false // Return false if result is null
    }

    override suspend fun getEventsForUser(userId: String, onFailure: (Exception) -> Unit): List<AnalyticsEvent> {
        return network.safeApiCallSuspend(
            apiCall = {
                // Query Firestore for events related to the specified user
                val querySnapshot = analyticsCollection
                    .whereEqualTo(FirestoreFields.USER_ID, userId)
                    .get().await()
                querySnapshot.documents.mapNotNull { it.toObject<AnalyticsEvent>() }
            },
            onFailure = { exception ->
                onFailure(exception)
                exception.message?.let { log(it) }
            }
        ) ?: emptyList() // Return an empty list if an error occurs
    }

    override suspend fun getEventById(eventId: String, onFailure: (Exception) -> Unit): AnalyticsEvent? {
        return network.safeApiCallSuspend(
            apiCall = {
                // Retrieve a specific event by its ID
                val documentSnapshot = analyticsCollection.document(eventId).get().await()
                documentSnapshot.toObject<AnalyticsEvent>()
            },
            onFailure = { exception ->
                onFailure(exception)
                exception.message?.let { log(it) }
            }
        )
    }

    override suspend fun generateEventId(): String {
        // Create a new document reference in the analytics collection and return its ID
        return analyticsCollection.document().id
    }

    private fun log(message: String) {
        logger.error("${ErrorMessages.ANALYTICS_API} : $message")
    }
}
