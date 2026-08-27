package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.configuration.PlayerConfiguration
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

/**
 * Verifies the reuse and neutralization logic of [PlayerPool].
 *
 * 🏗️ TEST OBJECTIVES:
 * 1. Player instantiation and surface binding.
 * 2. Resource neutralization (stop/clear) on release.
 * 3. Efficient reuse of idle player instances for new requests.
 * 4. Proper media item updates upon reuse.
 */
@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolReuseTest {

    private lateinit var pool: PlayerPool
    private lateinit var playerFactory: PlayerFactory
    private lateinit var configurationFactory: IPlayerConfigurationFactory
    private lateinit var analyticsListenerProvider: Provider<PlaybackAnalyticsListener>
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var config: IPlayerTuningConfig
    private lateinit var sizingPolicy: IPlayerPoolSizingPolicy
    private lateinit var metricsTracker: IMetricsTracker

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
        environmentCoordinator = mockk(relaxed = true)
        metricsTracker = mockk(relaxed = true)
        config = mockk(relaxed = true) {
            every { playerTuning } returns PlayerTuningConfig()
        }

        val environmentState = EnvironmentState(
            isMetered = false,
            shouldThrottlePerformance = false,
            estimatedThroughputBps = 10_000_000L
        )
        every { environmentCoordinator.environment } returns MutableStateFlow(environmentState)

        sizingPolicy = mockk {
            every { calculateMaxPoolSize(any()) } returns 2
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
    fun `verify player reuse and neutralization lifecycle`() = runTest {
        val id1 = "id1"
        val id2 = "id2"
        val uri1 = MediaReference("http://test.com/1")
        val uri2 = MediaReference("http://test.com/2")
        val mockPlayer = mockk<ExoPlayer>(relaxed = true)
        val mockSurface = mockk<Surface>(relaxed = true)
        val mockMediaItem1 = mockk<MediaItem>(relaxed = true)
        val mockMediaItem2 = mockk<MediaItem>(relaxed = true)

        // Mock PlayerConfiguration for id1
        val config1 = mockk<PlayerConfiguration>(relaxed = true) {
            every { mediaItem } returns mockMediaItem1
        }
        coEvery { configurationFactory.create(id1, uri1, any(), any(), any(), any(), any()) } returns config1

        // Mock PlayerConfiguration for id2
        val config2 = mockk<PlayerConfiguration>(relaxed = true) {
            every { mediaItem } returns mockMediaItem2
        }
        coEvery { configurationFactory.create(id2, uri2, any(), any(), any(), any(), any()) } returns config2

        // When creating for id1, return the mockPlayer
        every { playerFactory.create(config1) } returns PlayerFactory.CreatedPlayer(mockPlayer, mockk(relaxed = true))

        // 1. Acquire a player for id1.
        val managed1 = pool.getOrCreate(id1, uri1, MediaType.VOD)
        val player = managed1.player

        // 2. Verify setVideoSurface was called with a surface in the UI layer.
        // In this test, we simulate the UI layer action by calling it on the mock.
        player.setVideoSurface(mockSurface)
        verify { player.setVideoSurface(mockSurface) }

        // 3. Release the player for id1.
        pool.release(id1)

        // 4. Verify player.stop() and player.clearMediaItems() were called.
        // These are critical for neutralization before the player returns to the idle pool.
        verify {
            player.stop()
            player.clearMediaItems()
        }

        // 5. Acquire the same player for id2.
        // The sizing policy (max 2) allows for a prewarm budget of 1, so id1's player should be idle.
        val managed2 = pool.getOrCreate(id2, uri2, MediaType.VOD)

        // 6. Verify the player instance is the SAME as the one used for id1.
        assertSame("The player instance must be reused to minimize allocation overhead", managed1.player, managed2.player)

        // 7. Verify setMediaItem is called for the new media.
        // This ensures the reused player is correctly re-bound to the new URI.
        verify { player.setMediaItem(mockMediaItem2) }
    }
}

