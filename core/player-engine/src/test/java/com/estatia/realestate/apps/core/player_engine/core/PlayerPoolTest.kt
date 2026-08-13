package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.analytics.PlaybackAnalyticsListener
import com.estatia.realestate.apps.core.player_engine.configuration.IPlayerConfigurationFactory
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import javax.inject.Provider
import androidx.core.net.toUri

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerPoolTest {

    private lateinit var pool: PlayerPool
    private lateinit var playerFactory: PlayerFactory
    private lateinit var configurationFactory: IPlayerConfigurationFactory
    private lateinit var analyticsListenerProvider: Provider<PlaybackAnalyticsListener>
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var configProvider: IConfigProvider
    private lateinit var sizingPolicy: IPlayerPoolSizingPolicy
    private val testScope = TestScope()

    @Before
    @Suppress("UseKtx")
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk(relaxed = true)
        mockkStatic("androidx.core.net.UriKt")

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { mockLooper.thread } returns Thread.currentThread()
        every { mockLooper.getThread() } returns Thread.currentThread()
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
        configProvider = mockk(relaxed = true) {
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
            configProvider,
            testScope,
            sizingPolicy
        )
    }

    @Test
    fun `pool size is initially empty`() {
        assertEquals(0, pool.debugPlayerCount)
    }

    @Test
    fun `getOrCreate adding new player increases count`() = runTest {
        val mediaId = "id1"
        val uri = "".toUri()
        
        pool.getOrCreate(mediaId, uri, MediaType.VOD)
        
        assertEquals(1, pool.debugPlayerCount)
    }

    @Test
    fun `pool respects maxPoolSize via eviction`() = runTest {
        val uri = "".toUri()
        
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
