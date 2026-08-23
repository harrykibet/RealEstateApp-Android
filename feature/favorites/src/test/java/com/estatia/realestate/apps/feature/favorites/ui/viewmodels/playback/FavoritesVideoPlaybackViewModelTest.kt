package com.estatia.realestate.apps.feature.favorites.ui.viewmodels.playback

import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.player_engine.core.VideoPlaybackCoordinator
import com.estatia.realestate.apps.core.player_engine.utils.EnvironmentCoordinator
import com.estatia.realestate.apps.core.player_ui.state.PlayerUiState
import com.estatia.realestate.apps.core.model.common.MediaReference
import io.mockk.every
import io.mockk.mockk
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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

        every { userRepository.userData } returns flowOf(mockk(relaxed = true))
        every { environmentCoordinator.environment } returns MutableStateFlow(mockk(relaxed = true))
        every { environmentCoordinator.meteredConnectionDetected } returns MutableSharedFlow()

        viewModel = FavoritesVideoPlaybackViewModel(coordinator, environmentCoordinator, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initially Idle`() = runTest {
        assertEquals(PlayerUiState.Idle, viewModel.uiState.value)
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
