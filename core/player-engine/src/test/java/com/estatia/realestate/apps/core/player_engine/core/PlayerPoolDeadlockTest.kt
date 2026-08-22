package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import javax.inject.Provider
import androidx.core.net.toUri
import kotlin.time.Duration.Companion.milliseconds

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolDeadlockTest {

    private lateinit var pool: PlayerPool
    private lateinit var playerFactory: PlayerFactory
    private lateinit var configurationFactory: IPlayerConfigurationFactory
    private lateinit var analyticsListenerProvider: Provider<PlaybackAnalyticsListener>
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var config: IPlayerTuningConfig
    private lateinit var sizingPolicy: IPlayerPoolSizingPolicy
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    @Suppress("UseKtx")
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk(relaxed = true)
        mockkStatic("androidx.core.net.UriKt")

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { mockLooper.thread } returns Thread.currentThread()
        every { mockLooper.thread } returns Thread.currentThread()
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper

        playerFactory = mockk(relaxed = true)
        configurationFactory = mockk(relaxed = true)
        analyticsListenerProvider = mockk {
            every { get() } returns mockk(relaxed = true)
        }
        environmentCoordinator = mockk(relaxed = true) {
            every { environment.value } returns mockk(relaxed = true)
        }
        config = mockk(relaxed = true) {
            every { playerTuning } returns PlayerTuningConfig()
        }
        sizingPolicy = mockk {
            every { calculateMaxPoolSize(any()) } returns 1 // Constraint to 1 for easier deadlock repro
        }

        pool = PlayerPool(
            playerFactory,
            configurationFactory,
            analyticsListenerProvider,
            environmentCoordinator,
            config,
            testScope,
            sizingPolicy
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `urgent request promotes in-flight non-urgent request and prevents hang`() = runTest {
        val mediaId = "race_id"
        val uri = "".toUri()
        
        // 1. Simulate a slow non-urgent prewarm
        coEvery { configurationFactory.create(mediaId, any(), any(), any(), any(), any()) } coAnswers {
            delay(1000.milliseconds) // Artificial suspension window
            mockk(relaxed = true)
        }
        
        // Launch non-urgent prewarm
        val prewarmJob = launch {
            pool.prewarm(mediaId, uri, MediaType.VOD, urgent = false)
        }
        
        // 2. Mid-flight, an urgent request arrives for the SAME ID
        // It should promote the existing task and wait for it.
        val urgentResult = pool.getOrCreate(mediaId, uri, MediaType.VOD)
        
        assertNotNull("Urgent request should succeed after promotion!", urgentResult)
        prewarmJob.join()
    }
}
