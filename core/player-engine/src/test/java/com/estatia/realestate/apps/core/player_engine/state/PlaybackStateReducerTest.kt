package com.estatia.realestate.apps.core.player_engine.state

import android.os.SystemClock
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackStateReducerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var reducer: PlaybackStateReducer

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 0L
        reducer = PlaybackStateReducer(testScope)
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
    fun `watchdog triggers Error if stuck in Buffering for 15s`() = testScope.runTest {
        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            // Advance time to just before timeout
            advanceTimeBy(14999.milliseconds)
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
            advanceTimeBy(11000.milliseconds)
            expectNoEvents()
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

            advanceTimeBy(16000.milliseconds)
            expectNoEvents()
        }
    }
}
