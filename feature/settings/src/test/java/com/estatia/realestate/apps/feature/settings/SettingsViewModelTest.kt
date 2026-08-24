package com.estatia.realestate.apps.feature.settings

import app.cash.turbine.test
import com.estatia.realestate.apps.core.domain.repository.IUserRepository
import com.estatia.realestate.apps.core.testing.assertions.assertState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var userRepository: IUserRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        userRepository = mockk()
    }

    @Test
    fun `settingsUiState eventually becomes Success`() = runTest {
        every { userRepository.userData } returns MutableStateFlow(mockk(relaxed = true))
        
        viewModel = SettingsViewModel(userRepository)

        viewModel.settingsUiState.test {
            // Initial state
            assertTrue(awaitItem() is SettingsUiState.Loading)
            
            // Using platform assertion for the next state
            viewModel.settingsUiState.assertState { this is SettingsUiState.Success }
            awaitItem() // Consume
        }
    }
}
