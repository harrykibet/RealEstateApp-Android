package com.estatia.realestate.apps.core.intelligence

import com.estatia.realestate.apps.core.testing.fake.analytics.FakeEngagementRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class EngagementSignalProcessorTest {

    private lateinit var engagementRepository: FakeEngagementRepository
    private lateinit var processor: EngagementSignalProcessor

    @Before
    fun setup() {
        engagementRepository = FakeEngagementRepository()
        processor = EngagementSignalProcessor(engagementRepository)
    }

    @Test
    fun `processViewingSession records signal in witness`() = runTest {
        val mediaId = "video_123"
        val watchTime = 5000L
        val loops = 2

        processor.processViewingSession(mediaId, watchTime, loops)

        // Verify via high-performance Witness
        engagementRepository.witness.assertContains(
            FakeEngagementRepository.EngagementSignal.Watch(mediaId, watchTime, loops)
        )
    }

    @Test
    fun `processViewingSession handles extreme watch times gracefully`() = runTest {
        // 🧪 Edge Case: 24h watch time (maybe a background leak or buggy timer)
        val extremeTime = 24 * 60 * 60 * 1000L
        
        processor.processViewingSession("buggy_vid", extremeTime, 1)

        engagementRepository.witness.assertContains(
            FakeEngagementRepository.EngagementSignal.Watch("buggy_vid", extremeTime, 1)
        )
    }
}
