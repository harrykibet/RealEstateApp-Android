package com.estatia.realestate.apps

import app.cash.turbine.test
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IUserRepository
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    private lateinit var userRepository: IUserRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var viewModel: MainActivityViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val userDataFlow = MutableStateFlow(
        UserData(
            bookmarkedProperties = emptySet(),
            viewedProperties = emptySet(),
            followedProperties = emptySet(),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = false
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        authRepository = mockk()
        
        every { userRepository.userData } returns userDataFlow
        every { authRepository.isUserAuthenticated() } returns flowOf(true)
        
        viewModel = MainActivityViewModel(userRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiStateReflectsUserData() = runTest {
        viewModel.uiState.test {
            // Initial state from stateIn initialValue
            assertEquals(MainActivityViewModel.MainActivityUiState.Loading, awaitItem())

            // Then it should collect from the flow
            val successState = awaitItem()
            assert(successState is MainActivityViewModel.MainActivityUiState.Success)
            assertEquals(
                false,
                (successState as MainActivityViewModel.MainActivityUiState.Success).userData.useDynamicColor
            )

            // Update user data
            userDataFlow.value = userDataFlow.value.copy(useDynamicColor = true)
            val updatedState = awaitItem()
            assertEquals(
                true,
                (updatedState as MainActivityViewModel.MainActivityUiState.Success).userData.useDynamicColor
            )
        }
    }
}
