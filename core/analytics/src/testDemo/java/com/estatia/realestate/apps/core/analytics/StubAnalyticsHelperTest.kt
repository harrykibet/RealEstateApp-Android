package com.estatia.realestate.apps.core.analytics

import android.util.Log
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent as FirebaseAnalyticsEvent
import com.estatia.realestate.apps.core.model.system.DeviceInfo
import com.estatia.realestate.apps.core.testing.fake.analytics.RecordingAnalyticsTracker
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class StubAnalyticsHelperTest {

    private lateinit var analyticsTracker: RecordingAnalyticsTracker
    private lateinit var stubAnalyticsHelper: AnalyticsHelper

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        analyticsTracker = RecordingAnalyticsTracker()
        stubAnalyticsHelper = AnalyticsHelper(analyticsTracker)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `logEvent with structured event records in witness`() = runTest {
        // Given
        val event = FirebaseAnalyticsEvent(
            eventId = "test_id",
            eventType = "test_type",
            userId = "user_123",
            timestamp = 123456789L,
            metadata = emptyMap(),
            deviceInfo = DeviceInfo("Android", "Chrome", "Mobile", "1080x1920", "1.0.0"),
            userLocation = null
        )

        // When
        stubAnalyticsHelper.logEvent(event)

        // Then: Verify via high-performance Witness
        analyticsTracker.witness.assertContains(
            RecordingAnalyticsTracker.LoggedEvent.Domain(event)
        )
    }

    @Test
    fun `logEvent with local analytics event logs to logcat without crash`() = runTest {
        // Given
        val event = AnalyticsEvent(
            type = "local_event",
            extras = listOf(AnalyticsEvent.Param("key", "value"))
        )

        // When
        stubAnalyticsHelper.logEvent(event)

        // Then: Verify no crash occurred (implied by test completion)
    }
}
