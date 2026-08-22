package com.estatia.realestate.apps.core.analytics

import android.util.Log
import com.estatia.realestate.apps.core.domain.analytics.IAnalyticsTracker
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import com.estatia.realestate.apps.core.model.system.DeviceInfo
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class StubAnalyticsHelperTest {

    private lateinit var analyticsRepository: IAnalyticsTracker
    private lateinit var stubAnalyticsHelper: AnalyticsHelper

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        analyticsRepository = mockk(relaxed = true)
        stubAnalyticsHelper = AnalyticsHelper(analyticsRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun logEventWithFirebaseAnalyticsEventShouldCallRepository() {
        runTest {
            // Given
            val event = FirebaseAnalyticsEvent(
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

            // When
            stubAnalyticsHelper.logEvent(event)

            // Then
            coVerify { analyticsRepository.logEvent(event) }
        }
    }

    @Test
    fun logEventWithLocalAnalyticsEventShouldNotCrash() {
        runTest {
            // Given
            val event = AnalyticsEvent(
                type = "local_event",
                extras = listOf(AnalyticsEvent.Param("key", "value"))
            )

            // When
            stubAnalyticsHelper.logEvent(event)

            // Then - No crash expected, since it only logs to logcat
        }
    }
}
