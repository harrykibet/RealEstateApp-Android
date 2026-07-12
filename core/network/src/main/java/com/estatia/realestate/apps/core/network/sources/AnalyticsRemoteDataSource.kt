package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.errors.Errors
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import com.estatia.realestate.apps.core.common.interfaces.ILocationUtils
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.network.db_names.FirestoreCollections
import com.estatia.realestate.apps.core.network.db_names.FirestoreFields
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.IApiExecutor
import com.estatia.realestate.apps.core.network.interfaces.IAuthRemoteDataSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AnalyticsRemoteDataSource @Inject constructor(
    db: FirebaseFirestore,
    private val apiExecutor: IApiExecutor,
    private val logger: LoggerInterface,
    private val deviceUtils: IDeviceUtils,
    private val authApi: IAuthRemoteDataSource,
    private val locationUtils: ILocationUtils,
    private val firebaseAnalytics: FirebaseAnalytics
) : IAnalyticsRemoteDataSource {


    private val analyticsCollection =
        db.collection(FirestoreCollections.ANALYTICS)


    override suspend fun logEvent(
        event: AnalyticsEvent,
        onFailure: (Exception) -> Unit
    ): Boolean {

        return apiExecutor.execute {

            analyticsCollection
                .document(event.eventId)
                .set(event)
                .await()

            true

        }.fold(
            onSuccess = {
                true
            },
            onFailure = { exception ->

                handleFailure(
                    exception,
                    onFailure
                )

                false
            }
        )
    }


    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?,
        onFailure: (Exception) -> Unit
    ): Boolean {

        val metadata =
            customMetadata
                ?.toMutableMap()
                ?: mutableMapOf()


        metadata["message"] = message


        val userId =
            authApi.getCurrentUserId()
                ?: return false


        val event = AnalyticsEvent(
            eventId = generateEventId(),
            eventType = eventType,
            userId = userId,
            timestamp = System.currentTimeMillis(),
            metadata = metadata,
            deviceInfo = deviceUtils.getDeviceInfo(),
            userLocation = locationUtils.getLocationInfo()
        )


        return logEvent(
            event,
            onFailure
        )
    }


    override suspend fun getEventsForUser(
        userId: String,
        onFailure: (Exception) -> Unit
    ): List<AnalyticsEvent> {


        return apiExecutor.execute {

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


        }.fold(

            onSuccess = { events ->
                events
            },

            onFailure = { exception ->

                handleFailure(
                    exception,
                    onFailure
                )

                emptyList()
            }
        )
    }


    override suspend fun getEventById(
        eventId: String,
        onFailure: (Exception) -> Unit
    ): AnalyticsEvent? {


        return apiExecutor.execute {

            analyticsCollection
                .document(eventId)
                .get()
                .await()
                .toObject<AnalyticsEvent>()

        }.fold(

            onSuccess = { event ->
                event
            },

            onFailure = { exception ->

                handleFailure(
                    exception,
                    onFailure
                )

                null
            }
        )
    }


    override suspend fun generateEventId(): String {

        return analyticsCollection
            .document()
            .id
    }


    private fun handleFailure(
        exception: Exception,
        onFailure: (Exception) -> Unit
    ) {

        logger.e(
            "${Errors.ANALYTICS_REPO}: ${exception.message}",
            exception
        )

        onFailure(exception)
    }
}