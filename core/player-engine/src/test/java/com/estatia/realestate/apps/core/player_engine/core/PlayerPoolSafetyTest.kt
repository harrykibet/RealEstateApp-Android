package com.estatia.realestate.apps.core.player_engine.core

import android.os.Looper
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.domain.config.IPlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolSafetyTest {

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
            every { calculateMaxPoolSize(any()) } returns 2
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
    fun `trimIfNeeded respects pinnedIds and does not evict visible players`() = runTest {
        val uri = MediaReference("http://test.com")
        
        // 1. Fill the pool to max (2)
        pool.getOrCreate("visible_1", uri, MediaType.VOD)
        pool.getOrCreate("visible_2", uri, MediaType.VOD)
        
        assertEquals(2, pool.debugPlayerCount)
        
        // 2. Try to add a 3rd player (which would normally evict the LRU)
        // BUT we pin "visible_1" and "visible_2"
        pool.updatePinnedIds(setOf("visible_1", "visible_2"))
        
        // Pool size should stay at 2 because everything is pinned
        assertEquals(2, pool.debugPlayerCount)
        
        // Now add a 3rd one. It will force the pool to 3 temporarily
        // because prewarm/getOrCreate allows one slot above capacity before trim.
        // Wait, prewarm calls trimIfNeeded AFTER insertion.
        pool.getOrCreate("new_speculative", uri, MediaType.VOD)
        
        // It should still be at 3 because everyone is pinned (visible_1, visible_2)
        // AND the new one is temporarily "the excludeMediaId" of the creation logic if urgent?
        // No, I changed it to trimIfNeeded(pinnedMediaIds).
        // Since new_speculative is NOT in pinnedMediaIds, it will be evicted.
        assertEquals(2, pool.debugPlayerCount)
        assertNotNull(pool.get("visible_1"))
        assertNotNull(pool.get("visible_2"))
        assertEquals(null, pool.get("new_speculative"))
    }
}
