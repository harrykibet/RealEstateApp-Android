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
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors
import javax.inject.Provider
import androidx.core.net.toUri

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolConcurrencyTest {

    private lateinit var pool: PlayerPool
    private val mainThread = Thread.currentThread()
    private val mainDispatcher = StandardTestDispatcher()
    private val ioDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
    private val engineScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk(relaxed = true)
        mockkStatic("androidx.core.net.UriKt")

        mockkStatic(Looper::class)
        val mainLooper = mockk<Looper>(relaxed = true)
        every { Looper.getMainLooper() } returns mainLooper
        every { Looper.myLooper() } answers {
            if (Thread.currentThread().name.contains("main", ignoreCase = true) || 
                Thread.currentThread() == mainThread) mainLooper else null
        }

        val playerFactory = mockk<PlayerFactory>(relaxed = true)
        val configurationFactory = mockk<IPlayerConfigurationFactory>(relaxed = true)
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
}
