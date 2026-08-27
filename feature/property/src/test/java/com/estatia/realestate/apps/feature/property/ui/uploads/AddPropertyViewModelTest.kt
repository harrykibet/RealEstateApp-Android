package com.estatia.realestate.apps.feature.property.ui.uploads

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.AddPropertyDraft
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class AddPropertyViewModelTest {

    private lateinit var repository: IPropertyRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var intelligenceService: IMediaIntelligenceService
    private lateinit var viewModel: AddPropertyViewModel
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val propertyData = PropertyData()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } answers { mockk<Uri>(relaxed = true) }

        repository = mockk()
        authRepository = mockk()
        intelligenceService = mockk(relaxed = true)
        
        savedStateHandle = mockk(relaxed = true)
        every { savedStateHandle.get<AddPropertyDraft>(any()) } returns null
        
        viewModel = AddPropertyViewModel(
            repository, 
            authRepository, 
            intelligenceService, 
            mockk(relaxed = true), 
            savedStateHandle, 
            propertyData
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    @Test
    fun `vision model timeout during amenity extraction does not block UI`() = runTest {
        val uri = mockk<Uri>(relaxed = true)
        
        coEvery { intelligenceService.extractAmenities(any()) } coAnswers {
            delay(10.seconds)
            emptyList()
        }

        viewModel.addImage(uri)
        
        viewModel.updateTitle("Villa")
        viewModel.draft.assertProperty("Villa") { title }
        
        advanceTimeBy(11.seconds)
        runCurrent()
        
        viewModel.draft.assertProperty(emptySet<String>()) { amenities }
    }

    @Test
    fun `saveProperty failure when repository returns error`() = runTest {
        every { authRepository.getCurrentUserId() } returns "user_1"
        viewModel.updateTitle("Villa")
        
        coEvery { repository.uploadProperty(any(), any(), any()) } returns 
            AppResult.Error(PropertyException.SafetyViolation("Abusive content"))

        var caughtException: Exception? = null
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = {})

        assertTrue(caughtException is PropertyException.SafetyViolation)
    }

    @Test
    fun `viewModel handles batch media generation correctly`() = runTest {
        val imageUri = mockk<Uri>(relaxed = true)
        val videoUri = mockk<Uri>(relaxed = true)
        
        viewModel.addImage(imageUri)
        viewModel.addVideo(videoUri)
        
        viewModel.allMedia.test {
            val current = awaitItem()
            assertEquals("Expected 2 media items", 2, current.size)
        }
    }
}
