package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfiguration
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class SeekingPersistenceTest {

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
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { mockLooper.thread } returns Thread.currentThread()
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper

        playerFactory = mockk(relaxed = true)
        configurationFactory = mockk(relaxed = true)
        analyticsListenerProvider = mockk {
            every { get() } returns mockk(relaxed = true)
        }
        
        val environmentState = EnvironmentState(
            isMetered = false,
            shouldThrottlePerformance = false,
            estimatedThroughputBps = 10_000_000L
        )
        environmentCoordinator = mockk(relaxed = true) {
            every { environment.value } returns environmentState
        }
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        config = mockk(relaxed = true) {
            every { playerTuning } returns PlayerTuningConfig()
        }
        
        // Force prewarmBudget to 0 by setting maxPoolSize to 1, 
        // ensuring release() doesn't add to idlePlayers and we get a fresh instance.
        sizingPolicy = mockk {
            every { calculateMaxPoolSize(any()) } returns 1
        }

        pool = PlayerPool(
            playerFactory,
            configurationFactory,
            analyticsListenerProvider,
            environmentCoordinator,
            config,
            metricsTracker,
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
    fun positionIsRestoredAfterPoolEviction() = runTest {
        val mediaId = "id1"
        val uri = MediaReference("http://test.com")
        val savedPosition = 30000L

        // 1. Create player and mock its current position
        val player1 = mockk<ExoPlayer>(relaxed = true)
        every { player1.currentPosition } returns savedPosition
        
        val playerConfig = mockk<PlayerConfiguration>(relaxed = true)
        io.mockk.coEvery { configurationFactory.create(any(), any(), any(), any(), any(), any(), any()) } returns playerConfig
        
        val createdPlayer1 = PlayerFactory.CreatedPlayer(player1, mockk(relaxed = true))
        every { playerFactory.create(any()) } returns createdPlayer1

        // 2. getOrCreate for id1
        pool.getOrCreate(mediaId, uri, MediaType.VOD)

        // 3. release("id1") - this should trigger capturing the position
        pool.release(mediaId)

        // 4. Mock a new player for the second creation
        val player2 = mockk<ExoPlayer>(relaxed = true)
        val createdPlayer2 = PlayerFactory.CreatedPlayer(player2, mockk(relaxed = true))
        every { playerFactory.create(any()) } returns createdPlayer2

        // 5. getOrCreate for id1 again
        pool.getOrCreate(mediaId, uri, MediaType.VOD)

        // 6. Verify seekTo was called on player2 with savedPosition
        verify { player2.seekTo(savedPosition) }
    }
}
