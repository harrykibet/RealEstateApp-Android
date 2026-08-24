package com.estatia.realestate.apps.feature.property.ui.uploads

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.PropertyData
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddPropertyViewModelTest {

    private lateinit var repository: IPropertyRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var intelligenceService: IMediaIntelligenceService
    private lateinit var viewModel: AddPropertyViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    private val propertyData = PropertyData()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        authRepository = mockk()
        intelligenceService = mockk(relaxed = true)
        viewModel = AddPropertyViewModel(repository, authRepository, intelligenceService, propertyData)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateTitle updates draft title`() {
        viewModel.updateTitle("Luxury Villa")
        viewModel.draft.assertProperty("Luxury Villa") { title }
    }

    @Test
    fun `saveProperty failure when not authenticated`() {
        every { authRepository.getCurrentUserId() } returns null
        var caughtException: Exception? = null
        
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = {})
        
        assertEquals("User not authenticated", caughtException?.message)
    }

    @Test
    fun `saveProperty failure when repository returns error`() = runTest {
        every { authRepository.getCurrentUserId() } returns "user_1"
        viewModel.updateTitle("Villa")
        
        coEvery { repository.uploadProperty(any(), any(), any()) } returns 
            AppResult.Error(PropertyException.SafetyViolation("Abusive content"))

        var caughtException: Exception? = null
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(caughtException is PropertyException.SafetyViolation)
    }
}
