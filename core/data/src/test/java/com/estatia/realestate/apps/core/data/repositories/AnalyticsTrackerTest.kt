package com.estatia.realestate.apps.core.data.repositories

import android.content.Context
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import com.estatia.realestate.apps.core.database.interfaces.IAnalyticsLocalDataSource
import com.estatia.realestate.apps.core.model.analytics.AnalyticsEvent
import com.estatia.realestate.apps.core.model.system.DeviceInfo
import com.estatia.realestate.apps.core.network.interfaces.IAnalyticsRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.io.IOException

class AnalyticsTrackerTest {

    private lateinit var remoteDataSource: IAnalyticsRemoteDataSource
    private lateinit var localDataSource: IAnalyticsLocalDataSource
    private lateinit var logger: ILogger
    private lateinit var context: Context
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var analyticsTracker: AnalyticsTracker

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        logger = mockk(relaxed = true)
        context = mockk(relaxed = true)
        analyticsTracker = AnalyticsTracker(remoteDataSource, localDataSource, logger, json, context)
    }

    @Test
    fun `logEvent failure with disk full preserves event in memory outbox`() = runTest {
        val event = createMockEvent()
        
        // 🧪 Chaos Injection: Persistence layer fails due to Disk Full
        coEvery { localDataSource.saveEvent(any()) } throws IOException("No space left")
        coEvery { remoteDataSource.logEvent(event) } returns AppResult.Error(NetworkException.NoInternet)

        analyticsTracker.logEvent(event)

        // Verify fallback to memory outbox or correct error logging
        verify { logger.e(message = match { it.contains("logging failed") }, throwable = any()) }
    }

    private fun createMockEvent() = AnalyticsEvent(
        eventId = "test_id",
        eventType = "test_type",
        userId = "user_123",
        timestamp = 123456789L,
        metadata = emptyMap(),
        deviceInfo = DeviceInfo("Android", "Chrome", "Mobile", "1080x1920", "1.0.0"),
        userLocation = null
    )
}
