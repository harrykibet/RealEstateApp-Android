package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.testing.coroutine.runConcurrent
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors
import javax.inject.Provider

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolConcurrencyTest {

    private lateinit var pool: PlayerPool
    private lateinit var configurationFactory: IPlayerConfigurationFactory
    private lateinit var playerFactory: PlayerFactory
    private val mainThread = Thread.currentThread()
    private val mainDispatcher = StandardTestDispatcher()
    private val ioDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    private val engineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)

        mockkStatic(Looper::class)
        val mainLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mainLooper
        every { Looper.myLooper() } answers {
            if (Thread.currentThread().name.contains("main", ignoreCase = true) || 
                Thread.currentThread() == mainThread) mainLooper else null
        }

        playerFactory = mockk<PlayerFactory>(relaxed = true)
        configurationFactory = mockk<IPlayerConfigurationFactory>(relaxed = true)
        val analyticsListenerProvider = mockk<Provider<PlaybackAnalyticsListener>> {
            every { get() } returns mockk(relaxed = true)
        }
        val environmentCoordinator = mockk<EnvironmentCoordinator>(relaxed = true) {
            every { environment } returns MutableStateFlow(
                EnvironmentState(
                    isMetered = false,
                    shouldThrottlePerformance = false,
                    estimatedThroughputBps = 10_000_000L
                )
            )
        }
        val config = mockk<IPlayerTuningConfig>(relaxed = true) {
            every { playerTuning } returns PlayerTuningConfig()
        }
        val sizingPolicy = mockk<IPlayerPoolSizingPolicy> {
            every { calculateMaxPoolSize(any()) } returns 10
        }

        pool = PlayerPool(
            playerFactory,
            configurationFactory,
            analyticsListenerProvider,
            environmentCoordinator,
            config,
            engineScope,
            sizingPolicy
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        engineScope.cancel()
        ioDispatcher.close()
    }

    @Test
    fun `concurrent prewarm calls from multiple threads do not crash and maintain consistency`() = runTest {
        val count = 20
        val maxPoolSize = 10
        
        // Using runConcurrent for better stress testing
        runConcurrent(
            *Array(count) { i ->
                suspend {
                    withContext(Dispatchers.Main) {
                        pool.prewarm("id_$i", MediaReference("http://test.com"), MediaType.VOD)
                    }
                }
            }
        )

        advanceUntilIdle()
        assertEquals(maxPoolSize, pool.debugPlayerCount)
    }

    @Test
    fun `cancellation of initiator does not cancel coalesced requests`() = runTest {
        val mediaId = "coalesced_id"
        
        coEvery { configurationFactory.create(any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            delay(1000)
            mockk(relaxed = true)
        }

        val initiatorJob = launch(Dispatchers.Main) {
            pool.prewarm(mediaId, MediaReference("http://test.com"), MediaType.VOD)
        }
        
        runCurrent()
        
        val coalescedDeferred = async(Dispatchers.Main) {
            pool.prewarm(mediaId, MediaReference("http://test.com"), MediaType.VOD)
        }
        
        runCurrent()
        initiatorJob.cancelAndJoin()
        
        advanceTimeBy(1500)
        runCurrent()
        
        val result = coalescedDeferred.await()
        assertTrue("Coalesced request should succeed despite initiator cancellation", result is PrewarmResult.Success)
        assertEquals(1, pool.debugPlayerCount)
    }
}
