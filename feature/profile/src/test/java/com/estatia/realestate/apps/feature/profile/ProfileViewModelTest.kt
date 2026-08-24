package com.estatia.realestate.apps.feature.profile

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.core.testing.generators.UserGenerator
import com.estatia.realestate.apps.feature.profile.ui.viewmodels.ProfileViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
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
        val mockUser = UserGenerator.generateUser(id = userId, name = "John Doe")
        
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { userRepository.getUserById(userId) } returns AppResult.Success(mockUser)

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertProperty(false) { isLoading }
        viewModel.uiState.assertProperty("John Doe") { name }
    }

    @Test
    fun `loadUserProfile handles transient timeout with automatic retry or error state`() = runTest {
        val userId = "user_123"
        every { authRepository.getCurrentUserId() } returns userId
        
        // 🧪 Chaos: 1. Timeout -> 2. Success (Simulated by manual retry in UI or internal logic)
        coEvery { userRepository.getUserById(userId) } returns AppResult.Error(NetworkException.Timeout)

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertProperty("Connection timed out") { error }
    }

    @Test
    fun `loadUserProfile when not authenticated updates state with error`() = runTest {
        every { authRepository.getCurrentUserId() } returns null

        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        viewModel = ProfileViewModel(authRepository, userRepository, metricsTracker)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.assertProperty("User not authenticated") { error }
    }
}
