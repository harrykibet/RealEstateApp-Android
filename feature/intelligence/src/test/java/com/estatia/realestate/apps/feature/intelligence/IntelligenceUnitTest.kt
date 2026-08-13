package com.estatia.realestate.apps.feature.intelligence

import com.estatia.realestate.apps.core.domain.interfaces.IAnalyticsTracker
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IntelligenceUnitTest {

    private lateinit var analyticsTracker: IAnalyticsTracker
    private lateinit var processor: EngagementSignalProcessor

    @Before
    fun setup() {
        analyticsTracker = mockk(relaxed = true)
        processor = EngagementSignalProcessor(analyticsTracker)
    }

    @Test
    fun `processViewingSession reports engagement to analytics tracker`() = runTest {
        val mediaId = "video_123"
        val watchTime = 5000L
        val loops = 2

        processor.processViewingSession(mediaId, watchTime, loops)

        coVerify {
            analyticsTracker.reportEngagement(
                mediaId = mediaId,
                watchPercent = watchTime.toFloat(),
                loopCount = loops
            )
        }
    }
}
