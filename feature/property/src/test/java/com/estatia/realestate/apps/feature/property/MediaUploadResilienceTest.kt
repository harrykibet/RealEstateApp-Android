package com.estatia.realestate.apps.feature.property

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.NetworkException
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.intelligence.IMediaIntelligenceService
import com.estatia.realestate.apps.core.testing.assertions.assertProperty
import com.estatia.realestate.apps.feature.property.ui.uploads.viewModels.AddPropertyViewModel
import com.estatia.realestate.apps.feature.property.utils.PropertyData
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaUploadResilienceTest {

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
    fun `upload retry succeeds after transient timeout`() = runTest {
        val userId = "user_1"
        every { authRepository.getCurrentUserId() } returns userId
        viewModel.updateTitle("Resilience Test")

        // 🧪 Scripted Chaos: 1. Timeout -> 2. Success
        coEvery { repository.uploadProperty(any(), any(), any()) } returnsMany listOf(
            AppResult.Error(NetworkException.Timeout),
            AppResult.Success("prop_success_id")
        )

        var caughtException: Exception? = null
        var successId: String? = null

        // 1st Attempt: Fails
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = { successId = it })
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(caughtException is NetworkException.Timeout)

        // 2nd Attempt: Succeeds (Retry logic simulation)
        caughtException = null
        viewModel.saveProperty(onFailure = { caughtException = it }, onSuccess = { successId = it })
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successId == "prop_success_id")
    }
}
