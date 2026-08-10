package com.estatia.realestate.apps.feature.auth.viewModels

import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.model.auth.AuthUserDomainModel
import com.estatia.realestate.apps.feature.auth.actions.SignUpAction
import com.estatia.realestate.apps.feature.auth.events.SignUpEvent
import com.estatia.realestate.apps.feature.auth.state.SignUpFormState
import io.mockk.coEvery
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
class SignUpViewModelTest {

    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: SignUpViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = SignUpViewModel(authRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        assertEquals(SignUpFormState(), viewModel.state.value)
    }

    @Test
    fun `UserNameChanged action updates state`() = runTest {
        viewModel.onAction(SignUpAction.UserNameChanged("Harry"))
        assertEquals("Harry", viewModel.state.value.userName)
    }

    @Test
    fun `Submit with missing fields shows error`() = runTest {
        viewModel.onAction(SignUpAction.Submit)
        assertEquals("Please fill all required fields", viewModel.state.value.error)
    }

    @Test
    fun `Successful signup without phone triggers email verification event`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val userType = "AGENT"
        
        viewModel.onAction(SignUpAction.EmailChanged(email))
        viewModel.onAction(SignUpAction.PasswordChanged(password))
        viewModel.onAction(SignUpAction.UserTypeChanged(userType))

        val authUser = AuthUserDomainModel(
            userId = "uid_1",
            email = email,
            displayName = null,
            isEmailVerified = false,
            phoneNumber = null,
            photoUrl = null
        )

        coEvery { authRepository.signUpWithEmail(email, password) } returns AppResult.Success(authUser)
        coEvery { authRepository.createOrUpdateUserProfile(any(), any()) } returns AppResult.Success(Unit)

        viewModel.events.test {
            // When
            viewModel.onAction(SignUpAction.Submit)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val event = awaitItem()
            assert(event is SignUpEvent.RequireEmailVerification)
            assertEquals(email, (event as SignUpEvent.RequireEmailVerification).email)
        }
    }

    @Test
    fun `Successful signup with phone triggers phone verification event`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val userType = "AGENT"
        val phone = "+254700000000"
        
        viewModel.onAction(SignUpAction.EmailChanged(email))
        viewModel.onAction(SignUpAction.PasswordChanged(password))
        viewModel.onAction(SignUpAction.UserTypeChanged(userType))
        viewModel.onAction(SignUpAction.PhoneChanged(phone))

        val authUser = AuthUserDomainModel(
            userId = "uid_1",
            email = email,
            displayName = null,
            isEmailVerified = false,
            phoneNumber = phone,
            photoUrl = null
        )

        coEvery { authRepository.signUpWithEmail(email, password) } returns AppResult.Success(authUser)
        coEvery { authRepository.createOrUpdateUserProfile(any(), any()) } returns AppResult.Success(Unit)

        viewModel.events.test {
            // When
            viewModel.onAction(SignUpAction.Submit)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val event = awaitItem()
            assert(event is SignUpEvent.RequirePhoneVerification)
            assertEquals(phone, (event as SignUpEvent.RequirePhoneVerification).phone)
        }
    }
}
