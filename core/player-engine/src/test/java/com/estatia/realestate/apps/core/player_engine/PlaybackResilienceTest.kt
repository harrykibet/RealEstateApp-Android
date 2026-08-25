package com.estatia.realestate.apps.core.player_engine

import androidx.media3.common.util.UnstableApi
import app.cash.turbine.test
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.testing_player.chaos.streaming.ChaosStreamingPipeline
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackResilienceTest {

    private val testScope = TestScope()
    private lateinit var reducer: PlaybackStateReducer
    private lateinit var chaosPipeline: ChaosStreamingPipeline

    @Before
    fun setup() {
        reducer = PlaybackStateReducer(testScope)
        chaosPipeline = ChaosStreamingPipeline(mockk<IStreamingPipeline>(relaxed = true))
    }

    @Test
    fun `stalled buffer triggers watchdog even if pipeline is warm`() = testScope.runTest {
        reducer.state.test {
            assertEquals(PlaybackStateReducer.State.Idle, awaitItem())

            reducer.dispatch(PlaybackStateReducer.Event.BufferingStarted)
            assertEquals(PlaybackStateReducer.State.Buffering, awaitItem())

            // 🧪 Chaos: Pipeline fails to provide data
            chaosPipeline.setFailSegments(true)

            // Advance time past watchdog timeout (7s)
            advanceTimeBy(8.seconds)
            
            val finalState = awaitItem()
            assert(finalState is PlaybackStateReducer.State.Error)
            assertEquals("Playback attempt timed out (Watchdog)", (finalState as PlaybackStateReducer.State.Error).error.message)
        }
    }
}
