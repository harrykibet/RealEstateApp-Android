package com.estatia.realestate.apps.feature.profile

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.testing.assertions.assertCurrentProperty
import com.estatia.realestate.apps.core.testing.fixtures.UserFixtures
import com.estatia.realestate.apps.feature.profile.ui.viewmodels.ProfileViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var userRepository: IUserRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        authRepository = mockk()
        userRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserProfile success updates state with generated user`() = runTest {
        val userId = "user_123"
        val mockUser = UserFixtures.build(id = userId, name = "John Doe")
        
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { userRepository.getUserById(userId) } returns AppResult.Success(mockUser)

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)

        viewModel.uiState.assertCurrentProperty(false) { isLoading }
        viewModel.uiState.assertCurrentProperty("John Doe") { name }
    }

    @Test
    fun `loadUserProfile timeout updates error state`() = runTest {
        val userId = "user_123"
        every { authRepository.getCurrentUserId() } returns userId
        
        coEvery { userRepository.getUserById(userId) } returns AppResult.Error(NetworkException.Timeout)

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)

        viewModel.uiState.assertCurrentProperty("Connection timed out") { error }
    }

    @Test
    fun `loadUserProfile when not authenticated updates state with error`() = runTest {
        every { authRepository.getCurrentUserId() } returns null

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)

        viewModel.uiState.assertCurrentProperty("User not authenticated") { error }
    }
}
