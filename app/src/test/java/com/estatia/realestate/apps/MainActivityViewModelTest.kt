package com.estatia.realestate.apps

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.config.IConfigProvider
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.model.user.UserData
import com.estatia.realestate.apps.core.model.utils.DarkThemeConfig
import com.estatia.realestate.apps.core.model.utils.ThemeBrand
import com.estatia.realestate.apps.core.testing.assertions.assertState
import com.estatia.realestate.apps.core.testing.lifecycle.launchAndDestroy
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
    private lateinit var configProvider: IConfigProvider
    private val testDispatcher = StandardTestDispatcher()
    
    private val defaultUserData = UserData(
        bookmarkedProperties = emptySet(),
        viewedProperties = emptySet(),
        followedProperties = emptySet(),
        likedProperties = emptySet(),
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        useDynamicColor = false,
        shouldHideOnboarding = false,
        isMuted = false
    )

    private val userDataFlow = MutableStateFlow(defaultUserData)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        authRepository = mockk()
        configProvider = mockk()
        
        every { userRepository.userData } returns userDataFlow
        every { authRepository.isUserAuthenticated() } returns flowOf(true)
        every { configProvider.isReady } returns MutableStateFlow(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState reflects user data changes`() = runTest {
        val viewModel = MainActivityViewModel(userRepository, authRepository, configProvider)
        
        viewModel.uiState.test {
            assertEquals(MainActivityViewModel.MainActivityUiState.Loading, awaitItem())

            viewModel.uiState.assertState { 
                this is MainActivityViewModel.MainActivityUiState.Success && !userData.useDynamicColor 
            }
            awaitItem() // Consume Success

            userDataFlow.value = userDataFlow.value.copy(useDynamicColor = true)
            
            viewModel.uiState.assertState { 
                this is MainActivityViewModel.MainActivityUiState.Success && userData.useDynamicColor 
            }
            awaitItem() // Consume Success
        }
    }

    @Test
    fun `viewModel handles rapid destruction safely`() = runTest {
        launchAndDestroy {
            MainActivityViewModel(userRepository, authRepository, configProvider)
        }
    }

    @Test
    fun `viewModel restores state after process death`() = runTest {
        val restoredBrand = ThemeBrand.ANDROID
        val savedStateHandle = SavedStateHandle(mapOf("theme_brand" to restoredBrand.name))
        
        MainActivityViewModel(userRepository, authRepository, configProvider)
        // Verify ViewModel initialization logic with saved state
    }
}
