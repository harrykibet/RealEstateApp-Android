package com.estatia.realestate.apps.feature.home

import android.net.Uri
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.model.player.EnvironmentState
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.testing.assertions.assertCurrentState
import com.estatia.realestate.apps.feature.home.ui.viewModels.playback.HomeVideoPlaybackViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import androidx.core.net.toUri

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HomeVideoPlaybackResilienceTest {

    private lateinit var coordinator: VideoPlaybackCoordinator
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var userRepository: IUserRepository
    private lateinit var viewModel: HomeVideoPlaybackViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val envFlow = MutableStateFlow(
        EnvironmentState(false, false, 10_000_000L)
    )

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        val mockUri = mockk<Uri>(relaxed = true) {
            every { scheme } returns "https"
            every { host } returns "test.mp4"
        }
        every { Uri.parse(any()) } returns mockUri

        Dispatchers.setMain(testDispatcher)
        coordinator = mockk(relaxed = true)
        environmentCoordinator = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)

        every { userRepository.userData } returns flowOf(mockk(relaxed = true))
        every { environmentCoordinator.environment } returns envFlow
        every { environmentCoordinator.meteredConnectionDetected } returns MutableSharedFlow()

        viewModel = HomeVideoPlaybackViewModel(coordinator, environmentCoordinator, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    @Test
    fun `uiState transitions to LowBandwidth when sustained low bandwidth is detected`() = runTest {
        val mediaId = "vid_1"
        val engineState = MutableStateFlow<PlaybackStateReducer.State>(PlaybackStateReducer.State.Buffering)
        every { coordinator.observeState(mediaId) } returns engineState

        // When: Media becomes visible
        viewModel.onPageVisible(mediaId, MediaReference("https://test.mp4"), 1.0f, emptyList(), emptyList(), null, null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Initial state is Buffering
        viewModel.uiState.assertCurrentState { this == PlayerUiState.Buffering }

        // When: Chaos Injection - Sustained Low Bandwidth
        envFlow.value = envFlow.value.copy(isSustainedLowBandwidth = true)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: UI surfaces informative LowBandwidth state
        viewModel.uiState.assertCurrentState { this == PlayerUiState.LowBandwidth }
    }
}
