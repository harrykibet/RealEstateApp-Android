package com.estatia.realestate.apps.feature.profile.ui.viewmodels

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.model.user.UserDomainModel
import com.estatia.realestate.apps.core.model.user.UserType
import com.estatia.realestate.apps.core.model.user.VerificationLevel
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
import org.junit.Assert.assertEquals
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
    fun `loadUserProfile success updates state`() = runTest {
        val userId = "user_123"
        val mockUser = UserDomainModel(
            userId = userId,
            name = "John Doe",
            email = "john@example.com",
            bio = "Real estate enthusiast",
            profilePictureUrl = null,
            phoneNumber = "123456789",
            userType = UserType.AGENT,
            verificationLevel = VerificationLevel.NONE,
            likedProperties = emptyList(),
            propertyCount = 5,
            followerCount = 10,
            followingCount = 20
        )
        
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { userRepository.getUserById(userId) } returns AppResult.Success(mockUser)

        viewModel = ProfileViewModel(authRepository, userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("John Doe", state.name)
        assertEquals(5, state.stats.propertyCount)
    }

    @Test
    fun `loadUserProfile when not authenticated updates state with error`() = runTest {
        every { authRepository.getCurrentUserId() } returns null

        viewModel = ProfileViewModel(authRepository, userRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("User not authenticated", state.error)
    }
}
