package com.estatia.realestate.apps.core.player_engine.core

import android.net.Uri
import android.os.Looper
import androidx.media3.common.util.UnstableApi
import com.estatia.realestate.apps.core.model.property.MediaType
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors

@UnstableApi
@OptIn(ExperimentalCoroutinesApi::class)
class PlayerManagerTest {

    private lateinit var orchestrator: PlaybackOrchestrator
    private lateinit var sessionCoordinator: MediaSessionCoordinator
    private lateinit var networkRecovery: NetworkRecoveryCoordinator
    private lateinit var audioFocusManager: AudioFocusManager
    private lateinit var environmentManager: PlayerEnvironmentManager
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var pool: PlayerPool
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
        every { Looper.getMainLooper() } returns mockLooper
        every { Looper.myLooper() } returns mockLooper
        
        orchestrator = mockk(relaxed = true)
        sessionCoordinator = mockk(relaxed = true)
        networkRecovery = mockk(relaxed = true)
        audioFocusManager = mockk(relaxed = true)
        environmentManager = mockk(relaxed = true)
        environmentCoordinator = mockk(relaxed = true) {
            every { environment } returns environmentFlow
        }
        pool = mockk(relaxed = true)

        playerManager = PlayerManager(
            orchestrator,
            sessionCoordinator,
            networkRecovery,
            audioFocusManager,
            environmentManager,
            environmentCoordinator,
            pool,
            testScope,
            playerDispatcher
        )
    }

    @Test
    fun `play calls orchestrator play and requests focus`() = testScope.runTest {
        // Given
        val mediaId = "media_1"
        val uri = mockk<Uri>()
        val mediaType = MediaType.VOD

        // When
        playerManager.play(mediaId, uri, mediaType)

        // Then
        coVerify { orchestrator.play(mediaId, uri, mediaType, any(), any(), any()) }
        verify { audioFocusManager.request() }
    }

    @Test
    fun `pause calls orchestrator pause and abandons focus`() = testScope.runTest {
        // When
        playerManager.pause()

        // Then
        verify { orchestrator.pauseCurrentPlayer() }
        verify { audioFocusManager.abandon() }
    }
}
