package com.estatia.realestate.apps.feature.auth.viewModels

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.RemoteServiceException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.feature.auth.state.AuthState
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
class LoginViewModelTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginWithEmailSuccessShouldUpdateStateToAuthenticated() = runTest {
        // Given
        val user = AuthUserDomainModel(
            userId = "123",
            email = "test@example.com",
            displayName = "Test User",
            isEmailVerified = true,
            phoneNumber = "123456789",
            photoUrl = null
        )
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Success(user)

        viewModel.authState.test {
            // Initial state
            assertEquals(AuthState.Idle, awaitItem())

            // When
            viewModel.loginWithEmail("test@example.com", "password")

            // Then
            assertEquals(AuthState.Loading, awaitItem())
            val finalState = awaitItem()
            assert(finalState is AuthState.Authenticated)
            assertEquals(user, (finalState as AuthState.Authenticated).user)
        }
    }

    @Test
    fun loginWithEmailSuccessButEmailNotVerifiedShouldUpdateStateToEmailVerificationRequired() = runTest {
        // Given
        val user = AuthUserDomainModel(
            userId = "123",
            email = "test@example.com",
            displayName = "Test User",
            isEmailVerified = false,
            phoneNumber = "123456789",
            photoUrl = null
        )
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Success(user)

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            viewModel.loginWithEmail("test@example.com", "password")
            assertEquals(AuthState.Loading, awaitItem())
            val finalState = awaitItem()
            assert(finalState is AuthState.EmailVerificationRequired)
            assertEquals("test@example.com", (finalState as AuthState.EmailVerificationRequired).email)
        }
    }

    @Test
    fun loginWithEmailFailureShouldUpdateStateToError() = runTest {
        // Given
        val exception = RemoteServiceException.Unknown(Exception("Login failed"))
        coEvery { authRepository.signInWithEmail("test@example.com", "password") } returns AppResult.Error(exception)

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            viewModel.loginWithEmail("test@example.com", "password")
            assertEquals(AuthState.Loading, awaitItem())
            val finalState = awaitItem()
            assert(finalState is AuthState.Error)
            assertEquals("Unknown remote service error", (finalState as AuthState.Error).message)
        }
    }

    @Test
    fun checkExistingSessionWhenAuthenticatedShouldUpdateState() = runTest {
        // Given
        val user = AuthUserDomainModel(
            userId = "123",
            email = "test@example.com",
            displayName = "Test User",
            isEmailVerified = true,
            phoneNumber = "123456789",
            photoUrl = null
        )
        every { authRepository.getCurrentUser() } returns user

        viewModel.authState.test {
            assertEquals(AuthState.Idle, awaitItem())
            viewModel.checkExistingSession()
            val finalState = awaitItem()
            assert(finalState is AuthState.Authenticated)
        }
    }
}
