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
import com.estatia.realestate.apps.core.testing.chaos.models.TestFailure
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
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolTest {

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
            every { calculateMaxPoolSize(any()) } returns 3
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
    fun `pool size is initially empty`() {
        assertEquals(0, pool.debugPlayerCount)
    }

    @Test
    fun `pool handles memory exhaustion chaos gracefully`() = runTest {
        // 🧪 Chaos Scenario: Memory Exhausted
        println("Testing behavior: ${TestFailure.MemoryExhausted}")
        
        val uri = MediaReference("http://test.com")
        // Policy would normally return 1 in this case
        every { sizingPolicy.calculateMaxPoolSize(any()) } returns 1
        
        pool.getOrCreate("id1", uri, MediaType.VOD)
        pool.getOrCreate("id2", uri, MediaType.VOD)
        
        // Pool should have evicted immediately to stay at size 1
        assertEquals(1, pool.debugPlayerCount)
    }

    @Test
    fun `getOrCreate adding new player increases count`() = runTest {
        val mediaId = "id1"
        val uri = MediaReference("http://test.com")
        
        pool.getOrCreate(mediaId, uri, MediaType.VOD)
        
        assertEquals(1, pool.debugPlayerCount)
    }

    @Test
    fun `pool respects maxPoolSize via eviction`() = runTest {
        val uri = MediaReference("http://test.com")
        
        pool.getOrCreate("id1", uri, MediaType.VOD)
        pool.getOrCreate("id2", uri, MediaType.VOD)
        pool.getOrCreate("id3", uri, MediaType.VOD)
        
        assertEquals(3, pool.debugPlayerCount)
        
        // This should evict "id1" (least recently used)
        pool.getOrCreate("id4", uri, MediaType.VOD)
        
        assertEquals(3, pool.debugPlayerCount)
        assertEquals(null, pool.get("id1"))
    }
}
