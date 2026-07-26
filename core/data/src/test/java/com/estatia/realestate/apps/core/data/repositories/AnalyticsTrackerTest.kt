package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.RemoteServiceException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.model.system.DeviceInfo
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AnalyticsTrackerTest {

    private lateinit var remoteDataSource: IAnalyticsRemoteDataSource
    private lateinit var logger: ILogger
    private lateinit var analyticsTracker: AnalyticsTracker

    @Before
    fun setup() {
        remoteDataSource = mockk()
        logger = mockk(relaxed = true)
        analyticsTracker = AnalyticsTracker(remoteDataSource, logger)
    }

    @Test
    fun logEventWithAnalyticsEventShouldCallRemoteDataSource() = runTest {
        // Given
        val event = AnalyticsEvent(
            eventId = "test_id",
            eventType = "test_type",
            userId = "user_123",
            timestamp = 123456789L,
            metadata = emptyMap(),
            deviceInfo = DeviceInfo(
                os = "Android",
                browser = "Chrome",
                deviceType = "Mobile",
                screenResolution = "1080x1920",
                appVersion = "1.0.0"
            ),
            userLocation = null
        )
        coEvery { remoteDataSource.logEvent(event) } returns AppResult.Success(Unit)

        // When
        analyticsTracker.logEvent(event)

        // Then
        coVerify { remoteDataSource.logEvent(event) }
    }

    @Test
    fun logEventWithAnalyticsEventFailureShouldLogErrorMessage() = runTest {
        // Given
        val event = AnalyticsEvent(
            eventId = "test_id",
            eventType = "test_type",
            userId = "user_123",
            timestamp = 123456789L,
            metadata = emptyMap(),
            deviceInfo = DeviceInfo(
                os = "Android",
                browser = "Chrome",
                deviceType = "Mobile",
                screenResolution = "1080x1920",
                appVersion = "1.0.0"
            ),
            userLocation = null
        )
        val exception = RemoteServiceException.Unknown(Exception("Network error"))
        coEvery { remoteDataSource.logEvent(event) } returns AppResult.Error(exception)

        // When
        analyticsTracker.logEvent(event)

        // Then
        coVerify { logger.e(message = "Analytics logging failed", throwable = exception) }
    }

    @Test
    fun logEventWithMessageShouldCallRemoteDataSource() = runTest {
        // Given
        val message = "test message"
        val eventType = "test_type"
        val metadata = mapOf("key" to "value")
        coEvery { remoteDataSource.logEvent(message, eventType, metadata) } returns AppResult.Success(Unit)

        // When
        analyticsTracker.logEvent(message, eventType, metadata)

        // Then
        coVerify { remoteDataSource.logEvent(message, eventType, metadata) }
    }
}
