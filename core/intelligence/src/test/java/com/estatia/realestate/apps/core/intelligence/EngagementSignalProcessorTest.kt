package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class IntelligenceUnitTest {

    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var processor: EngagementSignalProcessor

    @Before
    fun setup() {
        engagementRepository = mockk(relaxed = true)
        processor = EngagementSignalProcessor(engagementRepository)
    }

    @Test
    fun `processViewingSession reports engagement to repository`() = runTest {
        val mediaId = "video_123"
        val watchTime = 5000L
        val loops = 2

        processor.processViewingSession(mediaId, watchTime, loops)

        coVerify {
            engagementRepository.reportMediaWatch(
                mediaId = mediaId,
                watchTimeMs = watchTime,
                loopCount = loops
            )
        }
    }
}
