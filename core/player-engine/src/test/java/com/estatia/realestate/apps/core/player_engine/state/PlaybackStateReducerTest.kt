package com.estatia.realestate.apps.core.player_engine.state

import android.os.Looper
import android.os.SystemClock
import app.cash.turbine.test
import com.estatia.realestate.apps.core.testing.clock.TestClock
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackStateReducerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var reducer: PlaybackStateReducer
    private val testClock = TestClock(0L)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Looper::class)
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 0L

        val mockLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper

        reducer = PlaybackStateReducer(testScope, clock = { testClock.currentTimeMillis() })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        assertEquals(PlaybackStateReducer.State.Idle, reducer.state.value)
    }

    @Test
    fun `dispatch BufferingStarted transitions to Buffering`() = runTest {
        reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
        assertEquals(PlaybackStateReducer.State.Buffering, reducer.state.value)
    }

    @Test
    fun `watchdog triggers Error if stuck in Buffering for 7s`() = testScope.runTest {
        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            // Advance time to just before timeout
            advanceTimeBy(6999.milliseconds)
            expectNoEvents()

            // Advance past timeout
            advanceTimeBy(1.milliseconds)
            val state = awaitItem()
            assert(state is PlaybackStateReducer.State.Error)
            assertEquals("Playback attempt timed out (Watchdog)", (state as PlaybackStateReducer.State.Error).error.message)
        }
    }

    @Test
    fun `watchdog is cancelled if BufferingCompleted arrives before timeout`() = testScope.runTest {
        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            advanceTimeBy(5000.milliseconds)
            reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
            assertEquals(PlaybackStateReducer.State.Ready, awaitItem())

            // Advance past the original timeout
            advanceTimeBy(3000.milliseconds)
            expectNoEvents()
        }
    }

    @Test
    fun `rapid toggle Buffering and Ready maintains stability`() = testScope.runTest {
        reducer.state.test {
            awaitItem() // Idle
            repeat(10) {
                reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
                assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())
                reducer.dispatch(PlaybackStateReducer.Event.BufferingCompleted)
                assertEquals(PlaybackStateReducer.State.Ready, awaitItem())
            }
        }
    }

    @Test
    fun `watchdog is cancelled on Reset`() = testScope.runTest {
        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.Reset)
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            advanceTimeBy(8000.milliseconds)
            expectNoEvents()
        }
    }
}
