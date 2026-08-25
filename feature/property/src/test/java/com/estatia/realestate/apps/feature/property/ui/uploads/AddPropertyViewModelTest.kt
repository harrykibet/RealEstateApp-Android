package com.estatia.realestate.apps.feature.property.ui.uploads

import androidx.core.net.toUri
import app.cash.turbine.test
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.PropertyException
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.core.testing.clock.TestClock
import com.estatia.realestate.apps.core.testing.generators.MediaGenerator
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    private val testDispatcher = StandardTestDispatcher()
    private val testClock = TestClock(0L)
    
    private val propertyData = PropertyData()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        authRepository = mockk()
        intelligenceService = mockk(relaxed = true)
        val metricsTracker = mockk<com.estatia.realestate.apps.core.domain.analytics.IMetricsTracker>(relaxed = true)
        val savedStateHandle = androidx.lifecycle.SavedStateHandle()
        viewModel = AddPropertyViewModel(repository, authRepository, intelligenceService, metricsTracker, savedStateHandle, propertyData)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `vision model timeout during amenity extraction does not block UI`() = runTest {
        val uri = mockk<android.net.Uri>()
        
        // 🧪 Chaos: Intelligence service hangs for 10s (exceeding standard timeouts)
        coEvery { intelligenceService.extractAmenities(any()) } coAnswers {
            delay(10.seconds)
            emptyList()
        }

        viewModel.addImage(uri)
        
        // Verify UI is still responsive and title can be updated
        viewModel.updateTitle("Villa")
        viewModel.draft.assertProperty("Villa") { title }
        
        // Advance virtual time
        testDispatcher.scheduler.advanceTimeBy(11.seconds)
        
        // Amenities still empty (graceful failure/timeout handled)
        viewModel.draft.assertProperty(emptyList<String>()) { amenities }
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

    @Test
    fun `viewModel handles batch media generation correctly`() = runTest {
        val image = MediaGenerator.generateImage().value.toUri()
        val video = MediaGenerator.generateVideo().value.toUri()
        
        viewModel.addImage(image)
        viewModel.addVideo(video)
        
        viewModel.allMedia.test {
            val current = awaitItem()
            assertTrue(current.contains(image))
            assertTrue(current.contains(video))
        }
    }
}
