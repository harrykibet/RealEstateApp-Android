package com.estatia.realestate.apps.feature.favorites.ui.viewmodels.playback

import androidx.media3.common.PlaybackException
import app.cash.turbine.test
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.model.common.MediaReference
import com.estatia.realestate.apps.core.player_engine.state.PlaybackStateReducer
import com.estatia.realestate.apps.core.testing.assertions.assertState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
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
import android.net.Uri
import android.os.SystemClock

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesVideoPlaybackViewModelTest {

    private lateinit var coordinator: VideoPlaybackCoordinator
    private lateinit var environmentCoordinator: EnvironmentCoordinator
    private lateinit var userRepository: IUserRepository
    private lateinit var viewModel: FavoritesVideoPlaybackViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coordinator = mockk(relaxed = true)
        environmentCoordinator = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)

        mockkStatic(Uri::class)
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtime() } returns 0L
        every { Uri.parse(any()) } returns mockk {
            every { scheme } returns "http"
            every { host } returns "estatia.com"
        }

        every { userRepository.userData } returns flowOf(mockk(relaxed = true))
        every { environmentCoordinator.environment } returns MutableStateFlow(mockk(relaxed = true))
        every { environmentCoordinator.meteredConnectionDetected } returns MutableSharedFlow()

        viewModel = FavoritesVideoPlaybackViewModel(coordinator, environmentCoordinator, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `uiState initially Idle`() = runTest {
        viewModel.uiState.assertState { this == PlayerUiState.Idle }
    }

    @Test
    fun `Watchdog error triggers autoAdvance event for feed continuity`() = runTest {
        val mediaId = "id_1"
        val engineState = MutableStateFlow<PlaybackStateReducer.State>(PlaybackStateReducer.State.Idle)
        every { coordinator.observeState(mediaId) } returns engineState

        viewModel.onPageVisible(mediaId, MediaReference("http://test.mp4"), 1.0f, emptyList(), emptyList(), null, null)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.autoAdvanceEvent.test {
            // 🧪 Chaos Injection: Engine reports a Watchdog timeout
            val watchdogException = PlaybackException("Watchdog", null, PlaybackException.ERROR_CODE_UNSPECIFIED)
            engineState.value = PlaybackStateReducer.State.Error(watchdogException)
            
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify event emitted
            awaitItem()
            
            // UI state should NOT show error because it's suppressed by auto-advance
            viewModel.uiState.assertState { this !is PlayerUiState.Error }
        }
    }

    @Test
    fun `onPageVisible updates coordinator`() = runTest {
        val uri = MediaReference("https://estatia.com/test.mp4")
        viewModel.onPageVisible("id", uri, 0.5f, emptyList(), emptyList(), "Title", "Artist")
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify {
            coordinator.onPageVisible(any(), "id", uri, 0.5f, emptyList(), emptyList(), "Title", "Artist")
        }
    }
}
