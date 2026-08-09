package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import javax.inject.Inject


class AnalyticsTracker @Inject constructor(
    private val remoteDataSource: IAnalyticsRemoteDataSource,
    private val logger: ILogger
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

            is AppResult.Success -> Unit


            is AppResult.Error -> {

                logger.e(
                    message = "Analytics logging failed",
                    throwable = result.exception
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

            is AppResult.Success -> Unit


            is AppResult.Error -> {

                logger.e(
                    message = "Analytics logging failed",
                    throwable = result.exception
                )
            }
        }
    }



    override fun generateEventId(): String {

        return remoteDataSource.generateEventId()
    }
}
