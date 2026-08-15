package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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
import androidx.core.net.toUri

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolConcurrencyTest {

    private lateinit var pool: PlayerPool
    private lateinit var configurationFactory: IPlayerConfigurationFactory
    private val mainThread = Thread.currentThread()
    private val mainDispatcher = StandardTestDispatcher()
    private val ioDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    private val engineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
        mockkStatic(Uri::class)
        every { any<String>().toUri() } returns mockk(relaxed = true)
        mockkStatic("androidx.core.net.UriKt")

        mockkStatic(Looper::class)
        val mainLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mainLooper
        every { Looper.myLooper() } answers {
            if (Thread.currentThread().name.contains("main", ignoreCase = true) || 
                Thread.currentThread() == mainThread) mainLooper else null
        }

        val playerFactory = mockk<PlayerFactory>(relaxed = true)
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
        val configProvider = mockk<IConfigProvider>(relaxed = true) {
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
            configProvider,
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
        val jobs = mutableListOf<Deferred<PrewarmResult>>()
        
        // Simulate high scroll load with concurrent prewarms
        repeat(count) { i ->
            jobs.add(async(Dispatchers.Default) {
                // Must switch to Main to call pool methods as they checkConfinement
                withContext(Dispatchers.Main) {
                    pool.prewarm("id_$i", "".toUri(), MediaType.VOD)
                }
            })
        }

        // Wait for all requests and advance main dispatcher to process ensureIdlePlayers launches
        advanceUntilIdle()
        
        val results = jobs.awaitAll()
        assertEquals(maxPoolSize, results.filterIsInstance<PrewarmResult.Success>().size)
        assertEquals(count - maxPoolSize, results.filterIsInstance<PrewarmResult.Rejected>().size)
        assertEquals(maxPoolSize, pool.debugPlayerCount)
    }

    @Test
    fun `cancellation of initiator does not cancel coalesced requests`() = runTest {
        val mediaId = "coalesced_id"
        
        // Mock a slow creation
        coEvery { configurationFactory.create(any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            delay(1000)
            mockk(relaxed = true)
        }

        // Start initiator
        val initiatorJob = launch(Dispatchers.Main) {
            pool.prewarm(mediaId, "".toUri(), MediaType.VOD)
        }
        
        // Yield to let initiator start
        runCurrent()
        
        // Start coalesced request
        val coalescedDeferred = async(Dispatchers.Main) {
            pool.prewarm(mediaId, "".toUri(), MediaType.VOD)
        }
        
        // Yield to let coalesced start
        runCurrent()
        
        // Cancel initiator
        initiatorJob.cancelAndJoin()
        
        // Advance time to finish the work (protected by NonCancellable)
        advanceTimeBy(1500)
        runCurrent()
        
        // The coalesced request should succeed
        val result = coalescedDeferred.await()
        assertTrue("Coalesced request should succeed despite initiator cancellation", result is PrewarmResult.Success)
        assertEquals(1, pool.debugPlayerCount)
    }

    @Test
    fun `prewarm propagates cancellation and does not return it as Failure`() = runTest {
        val mediaId = "cancel_test"
        
        coEvery { configurationFactory.create(any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            delay(1000)
            mockk(relaxed = true)
        }

        val deferred = async(Dispatchers.Main) {
            pool.prewarm(mediaId, "".toUri(), MediaType.VOD)
        }
        
        runCurrent()
        delay(100)
        deferred.cancel()
        
        try {
            val result = deferred.await()
            fail("Should have been cancelled, but got $result")
        } catch (e: CancellationException) {
            // Expected: async propagates cancellation when awaited
        } catch (e: Throwable) {
            fail("Expected CancellationException, but got ${e.javaClass.simpleName}")
        }
    }
}
