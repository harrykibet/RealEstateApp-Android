package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.INetworkHandler
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnalyticsRemoteDataSource @Inject constructor(
    db: FirebaseFirestore,
    private val network: INetworkHandler,
    private val logger: LoggerInterface,
    private val deviceUtils: IDeviceUtils,
    private val authApi : IAuthRemoteDataSource,
    private val locationUtils: ILocationUtils,
    private val firebaseAnalytics: FirebaseAnalytics
) : IAnalyticsRemoteDataSource {

    private val analyticsCollection = db.collection(FirestoreCollections.ANALYTICS)

    override suspend fun logEvent(event: AnalyticsEvent, onFailure: (Exception) -> Unit): Boolean {
        return network.safeApiCallSuspend(
            apiCall = {
                // Save the event to FireStore
                analyticsCollection.document(event.eventId).set(event).await()
                true // Indicate success
            },
            onFailure = { exception ->
                onFailure(exception)
                exception.message?.let { log(it) }
            }
        ) ?: false // Return false if result is null
    }

    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?,
        onFailure: (Exception) -> Unit
    ): Boolean {
        val metadata = customMetadata?.toMutableMap() ?: mutableMapOf()
        metadata["message"] = message  // Add default message if not already present
        val deviceInfo = deviceUtils.getDeviceInfo()
        val locationInfo = locationUtils.getLocationInfo()

        val analyticsEvent = authApi.getCurrentUserId()?.let {
            AnalyticsEvent(
                eventId = generateEventId(),
                eventType = eventType,
                userId = it,
                timestamp = System.currentTimeMillis(),
                metadata = metadata,
                deviceInfo = deviceInfo,
                userLocation = locationInfo
            )
        }
        return logEvent(analyticsEvent!!, onFailure)
    }

    override suspend fun getEventsForUser(userId: String, onFailure: (Exception) -> Unit): List<AnalyticsEvent> {
        return network.safeApiCallSuspend(
            apiCall = {
                // Query FireStore for events related to the specified user
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
        logger.e("${Errors.ANALYTICS_REPO} : $message")
    }
}
