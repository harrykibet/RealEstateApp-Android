package com.estatia.realestate.apps.core.player_engine.core

import android.content.Context
import android.net.Uri
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.configuration.DynamicBitrateController
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.streaming.IStreamingPipeline
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import com.estatia.realestate.apps.core.player_engine.utils.IPlayerPoolSizingPolicy
import com.estatia.realestate.apps.core.network.interfaces.INetworkStateProvider
import androidx.core.net.toUri
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
    private lateinit var environmentManager: PlayerEnvironmentManager
    private lateinit var audioFocusManager: AudioFocusManager
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var sizingPolicy: IPlayerPoolSizingPolicy
    private lateinit var dynamicBitrateController: DynamicBitrateController
    private lateinit var networkStateProvider: INetworkStateProvider
    private lateinit var streamingPipeline: IStreamingPipeline
    private lateinit var context: Context
    private lateinit var playerManager: PlayerManager

    private val testScope = TestScope()
    private val playerDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val environmentFlow = MutableStateFlow(
        EnvironmentState(isMetered = false, shouldThrottlePerformance = false, estimatedThroughputBps = 1000000)
    )

    @Before
    @Suppress("UseKtx")
    fun setup() {
        mockkStatic(Uri::class)
        val mockUri = mockk<Uri>(relaxed = true)
        every { Uri.parse(any()) } returns mockUri

        mockkStatic(Looper::class)
        val mockLooper = mockk<Looper>(relaxed = true)
        every { mockLooper.thread } returns Thread.currentThread()
        every { mockLooper.thread } returns Thread.currentThread()
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper
        
        pool = mockk(relaxed = true)
        environmentManager = mockk(relaxed = true)
        audioFocusManager = mockk(relaxed = true)
        environmentCoordinator = mockk(relaxed = true) {
            every { environment } returns environmentFlow
        }
        networkStateProvider = mockk(relaxed = true)
        streamingPipeline = mockk(relaxed = true)
        context = mockk(relaxed = true)
        every { context.getSystemService(Context.AUDIO_SERVICE) } returns null

        playerManager = PlayerManager(
            context,
            pool,
            environmentManager,
            audioFocusManager,
            mockk(relaxed = true), // dynamicBitrateController
            environmentCoordinator,
            networkStateProvider,
            streamingPipeline,
            mockk(relaxed = true), // mediaSessionProvider
            testScope,
            playerDispatcher
        )
    }

    @After
    fun tearDown() {
        playerManager.shutdown()
    }

    @Test
    fun `play calls pool getOrCreate and plays the player`() = testScope.runTest {
        // Given
        val mediaId = "media_1"
        val uri = "".toUri()
        val mediaType = MediaType.VOD
        val mockPlayer = mockk<ExoPlayer>(relaxed = true)
        every { mockPlayer.applicationLooper } returns Looper.getMainLooper()

        val mockManagedPlayer = ManagedPlayer(
            mediaId, mediaType, mockPlayer, mockk(relaxed = true), PlaybackStateReducer(testScope)
        )
        coEvery { pool.getOrCreate(mediaId, any(), any(), any(), any(), any()) } returns mockManagedPlayer

        // When
        playerManager.play(mediaId, uri, mediaType)

        // Then
        verify { mockPlayer.play() }
    }
}
