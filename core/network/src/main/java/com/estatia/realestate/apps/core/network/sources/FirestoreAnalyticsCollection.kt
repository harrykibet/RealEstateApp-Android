package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.core.RetryConfigs
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirestoreAnalyticsCollection @Inject constructor(
    db: FirebaseFirestore,
    private val apiExecutor: IApiExecutor,
    private val deviceUtils: IDeviceUtils,
    private val authApi: IAuthRemoteDataSource,
    private val locationUtils: ILocationUtils
) : IAnalyticsRemoteDataSource {


    private val analyticsCollection =
        db.collection(FirestoreCollections.ANALYTICS)


    override suspend fun logEvent(
        event: AnalyticsEvent
    ): Result<Unit> {

        return apiExecutor.execute(RetryConfigs.ANALYTICS) {
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
            authApi.getCurrentUserId()
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

        return apiExecutor.execute(RetryConfigs.ANALYTICS) {

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

        return apiExecutor.execute(RetryConfigs.ANALYTICS) {

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