package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirestoreAnalyticsCollection @Inject constructor(
    database: FirebaseFirestore,
    private val networkClient: INetworkClient,
    private val deviceUtils: IDeviceUtils,
    private val authService: IAuthRemoteDataSource,
    private val locationUtils: ILocationUtils
) : IAnalyticsRemoteDataSource {


    private val analyticsCollection =
        database.collection(FirestoreCollections.ANALYTICS)


    override suspend fun logEvent(
        event: AnalyticsEvent
    ): Result<Unit> {

        return networkClient.execute {
            analyticsCollection
                .document(event.eventId)
                .set(event)
                .await()

        }
    }


    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ): Result<Unit> {

        val userId =
            authService.getCurrentUserId()
                ?: return Result.Failure(
                    AuthException.UserNotAuthenticated
                )


        val metadata =
            customMetadata
                ?.toMutableMap()
                ?: mutableMapOf()


        metadata["message"] = message


        val event = AnalyticsEvent(
            eventId = generateEventId(),
            eventType = eventType,
            userId = userId,
            timestamp = System.currentTimeMillis(),
            metadata = metadata,
            deviceInfo = deviceUtils.getDeviceInfo(),
            userLocation = locationUtils.getLocationInfo()
        )


        return logEvent(event)
    }


    override suspend fun getEventsForUser(
        userId: String
    ): Result<List<AnalyticsEvent>> {

        return networkClient.execute {

            analyticsCollection
                .whereEqualTo(
                    FirestoreFields.USER_ID,
                    userId
                )
                .get()
                .await()
                .documents
                .mapNotNull { document ->

                    document.toObject<AnalyticsEvent>()

                }
        }
    }


    override suspend fun getEventById(
        eventId: String
    ): Result<AnalyticsEvent?> {

        return networkClient.execute {

            analyticsCollection
                .document(eventId)
                .get()
                .await()
                .toObject<AnalyticsEvent>()

        }
    }


    override fun generateEventId(): String {

        return analyticsCollection
            .document()
            .id
    }
}