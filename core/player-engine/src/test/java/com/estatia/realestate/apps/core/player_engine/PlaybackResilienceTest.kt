package com.estatia.realestate.apps.core.player_engine

import android.os.Looper
import android.os.SystemClock
import androidx.media3.common.util.UnstableApi
import app.cash.turbine.test
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.testing.clock.TestClock
import com.estatia.realestate.apps.core.testing_player.chaos.streaming.ChaosStreamingPipeline
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackResilienceTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var reducer: PlaybackStateReducer
    private lateinit var chaosPipeline: ChaosStreamingPipeline
    private val testClock = TestClock(0L)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Looper::class)
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 0L

        val mockLooper = mockk<Looper>(relaxed = true)
        every { mockLooper.thread } returns Thread.currentThread()
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper

        reducer = PlaybackStateReducer(testScope, clock = { testClock.currentTimeMillis() })
        chaosPipeline = ChaosStreamingPipeline(mockk<IStreamingPipeline>(relaxed = true))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `stalled buffer triggers watchdog even if pipeline is warm`() = runTest(testDispatcher) {
        reducer = PlaybackStateReducer(backgroundScope, clock = { testClock.currentTimeMillis() })

        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            runCurrent()
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            // 🧪 Chaos: Pipeline fails to provide data
            chaosPipeline.setFailSegments(true)

            // Advance time past watchdog timeout (7s)
            advanceTimeBy(7001.milliseconds)
            runCurrent()
            
            val finalState = awaitItem()
            assert(finalState is PlaybackStateReducer.State.Error)
            assertEquals("Playback attempt timed out (Watchdog)", (finalState as PlaybackStateReducer.State.Error).error.message)
        }
    }
}
