package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import android.os.SystemClock
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.common.system.PerformanceMonitor
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.player.FeedNeighbor
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.streaming.WarmPriority
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class VideoPlaybackCoordinatorTest {

    private lateinit var coordinator: VideoPlaybackCoordinator
    private lateinit var playerController: IPlayerManager
    private lateinit var streamingPipeline: IStreamingPipeline
    private lateinit var performanceMonitor: PerformanceMonitor
    private lateinit var config: IPlayerTuningConfig
    private val testScope = TestScope()

    private val tuning = PlayerTuningConfig(
        dwellTimeDebounceMs = 100,
        jankAwareDebounceMs = 400,
        flingDebounceMs = 250,
        fastScrollThresholdMs = 300,
        flingCountThreshold = 3
    )

    private val isJankingFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 0L

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper

        playerController = mockk(relaxed = true)
        streamingPipeline = mockk(relaxed = true)
        performanceMonitor = mockk {
            every { isJanking } returns isJankingFlow
        }
        config = mockk {
            every { playerTuning } returns tuning
        }

        coordinator = VideoPlaybackCoordinator(
            playerController,
            streamingPipeline,
            performanceMonitor,
            config
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class, Looper::class)
    }

    @Test
    fun `onPageVisible schedules debounced playback`() = testScope.runTest {
        val mediaId = "video_1"
        val uri = MediaReference("http://test.com")

        coordinator.onPageVisible(this, mediaId, uri, 1.0f, emptyList(), emptyList())

        // Before debounce
        advanceTimeBy(50.milliseconds)
        coVerify(exactly = 0) { playerController.play(mediaId, any(), any(), any(), any()) }

        // After debounce
        advanceTimeBy(51.milliseconds)
        coVerify(exactly = 1) { playerController.play(mediaId, uri, MediaType.VOD, any(), any()) }
    }

    @Test
    fun `fast scrolling increases debounce and skips prewarming`() = testScope.runTest {
        val uri = MediaReference("http://test.com")
        
        // Simulate 3 fast scrolls (fling)
        repeat(tuning.flingCountThreshold) { i ->
            every { SystemClock.elapsedRealtime() } returns (i * 100L) // 100ms apart
            coordinator.onPageVisible(this, "video_$i", uri, 1.0f, emptyList(), emptyList())
        }

        val targetId = "video_final"
        every { SystemClock.elapsedRealtime() } returns (tuning.flingCountThreshold * 100L)
        coordinator.onPageVisible(this, targetId, uri, 1.0f, emptyList(), emptyList())

        // Standard debounce (100ms) should NOT fire
        advanceTimeBy(101.milliseconds)
        coVerify(exactly = 0) { playerController.play(targetId, any(), any(), any(), any()) }

        // Fling debounce (250ms) should fire
        advanceTimeBy(150.milliseconds)
        coVerify(exactly = 1) { playerController.play(targetId, uri, MediaType.VOD, any(), any()) }

        // Prewarming should have been skipped during fling
        verify(exactly = 0) { streamingPipeline.warm(any(), any(), match { it != WarmPriority.VISIBLE }) }
    }

    @Test
    fun `jank detection increases debounce time`() = testScope.runTest {
        isJankingFlow.value = true
        val mediaId = "video_jank"
        val uri = MediaReference("http://test.com")

        coordinator.onPageVisible(this, mediaId, uri, 1.0f, emptyList(), emptyList())

        // Even fling debounce (250ms) shouldn't fire
        advanceTimeBy(251.milliseconds)
        coVerify(exactly = 0) { playerController.play(mediaId, any(), any(), any(), any()) }

        // Jank-aware debounce (400ms) should fire
        advanceTimeBy(150.milliseconds)
        coVerify(exactly = 1) { playerController.play(mediaId, uri, MediaType.VOD, any(), any()) }
    }

    @Test
    fun `neighbor warming is triggered after debounce`() = testScope.runTest {
        val mediaId = "main_video"
        val uri = MediaReference("http://main.com")
        val prev = listOf(FeedNeighbor("prev_1", MediaReference("http://prev.com"), matchScore = 1.0f))
        val next = listOf(FeedNeighbor("next_1", MediaReference("http://next1.com"), matchScore = 1.0f), FeedNeighbor("next_2", MediaReference("http://next2.com"), matchScore = 1.0f))

        coordinator.onPageVisible(this, mediaId, uri, 1.0f, prev, next)

        advanceTimeBy(101.milliseconds)

        // Main video play
        coVerify { playerController.play(mediaId, any(), any(), any(), any()) }
        
        // Neighbor preloads and warming
        coVerify { playerController.preload("prev_1", any(), any(), any(), any()) }
        coVerify { playerController.preload("next_1", any(), any(), any(), any()) }
        
        verify { streamingPipeline.warm(mediaId, any(), WarmPriority.VISIBLE) }
        verify { streamingPipeline.warm("prev_1", any(), WarmPriority.PREVIOUS) }
        verify { streamingPipeline.warm("next_1", any(), WarmPriority.NEXT) }
        verify { streamingPipeline.warm("next_2", any(), WarmPriority.LOW) }
    }

    @Test
    fun `onPageVisible deduplicates identical mediaId`() = testScope.runTest {
        val mediaId = "video_1"
        val uri = MediaReference("http://test.com")
        every { playerController.activeMediaId } returns mediaId

        coordinator.onPageVisible(this, mediaId, uri, 1.0f, emptyList(), emptyList())

        advanceTimeBy(1000.milliseconds)
        coVerify(exactly = 0) { playerController.play(any(), any(), any(), any(), any()) }
    }
}
