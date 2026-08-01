package com.estatia.realestate.apps.core.player_engine.core

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerManagerTest {

    private lateinit var pool: PlayerPool
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var sizingPolicy: IPlayerPoolSizingPolicy
    private lateinit var dynamicBitrateController: DynamicBitrateController
    private lateinit var playerManager: PlayerManager

    private val testScope = TestScope()
    private val playerDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val environmentFlow = MutableStateFlow(
        EnvironmentState(isMetered = false, shouldThrottlePerformance = false, estimatedThroughputBps = 1000000)
    )

    @Before
    fun setup() {
        pool = mockk(relaxed = true)
        environmentCoordinator = mockk(relaxed = true) {
            every { environment } returns environmentFlow
        }
        sizingPolicy = mockk {
            every { calculateMaxPoolSize() } returns 3
        }
        dynamicBitrateController = mockk(relaxed = true)

        playerManager = PlayerManager(
            pool,
            environmentCoordinator,
            sizingPolicy,
            dynamicBitrateController,
            testScope,
            playerDispatcher
        )
    }

    @After
    fun tearDown() {
        playerManager.shutdown()
    }

    @Test
    fun `play calls pool getOrCreate and plays the player`() = runTest {
        // Given
        val mediaId = "media_1"
        val mediaType = MediaType.VOD
        val mockPlayer = mockk<ExoPlayer>(relaxed = true)
        val mockManagedPlayer = PlayerPool.ManagedPlayer(
            mediaId, mediaType, mockPlayer, mockk(relaxed = true), PlaybackStateReducer()
        )
        coEvery { pool.getOrCreate(mediaId, mediaType) } returns mockManagedPlayer

        // When
        playerManager.play(mediaId, mediaType)

        // Then
        verify { mockPlayer.play() }
    }

    @Test
    fun `environment update triggers pool resize and bitrate adjustment`() = runTest {
        // Given
        every { sizingPolicy.calculateMaxPoolSize() } returns 5
        
        // When
        environmentFlow.value = environmentFlow.value.copy(isMetered = true)

        // Then (wait for collection)
        // We use verify with timeout because PlayerManager uses a real background thread (playerDispatcher)
        verify(timeout = 2000) { pool.updateMaxPoolSize(5, any()) }
    }
}
