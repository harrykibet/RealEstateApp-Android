package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.errors.Result
import com.estatia.realestate.apps.core.common.interfaces.LoggerInterface
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.data.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import javax.inject.Inject


class AnalyticsTracker @Inject constructor(
    private val remoteDataSource: IAnalyticsRemoteDataSource,
    private val logger: LoggerInterface
) : IAnalyticsTracker {


    override suspend fun logEvent(
        message: String,
        eventType: String,
        customMetadata: Map<String, String>?
    ) {

        when(
            val result = remoteDataSource.logEvent(
                message = message,
                eventType = eventType,
                customMetadata = customMetadata
            )
        ) {

            is Result.Success -> Unit


            is Result.Failure -> {

                logger.e(
                    "Analytics logging failed",
                    result.exception
                )
            }
        }
    }



    override suspend fun logEvent(
        event: AnalyticsEvent
    ) {

        when(
            val result = remoteDataSource.logEvent(event)
        ) {

            is Result.Success -> Unit


            is Result.Failure -> {

                logger.e(
                    "Analytics logging failed",
                    result.exception
                )
            }
        }
    }



    override fun generateEventId(): String {

        return remoteDataSource.generateEventId()
    }
}