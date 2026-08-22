package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.analytics.IEngagementRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TogglePropertyLikeUseCaseTest {

    private lateinit var propertyRepository: IPropertyRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var engagementRepository: IEngagementRepository
    private lateinit var useCase: TogglePropertyLikeUseCase

    @Before
    fun setup() {
        propertyRepository = mockk()
        authRepository = mockk()
        engagementRepository = mockk(relaxed = true)
        useCase = TogglePropertyLikeUseCase(propertyRepository, authRepository, engagementRepository)
    }

    @Test
    fun `invoke should return Error when user not authenticated`() = runTest {
        every { authRepository.getCurrentUserId() } returns null
        
        val result = useCase("prop_1", false)
        
        assertTrue(result is AppResult.Error)
        assertEquals(AuthException.UserNotAuthenticated, (result as AppResult.Error).exception)
    }

    @Test
    fun `invoke should call likeProperty and reportInteraction when not currently liked`() = runTest {
        val userId = "user_1"
        val propertyId = "prop_1"
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { propertyRepository.likeProperty(userId, propertyId) } returns AppResult.Success(Unit)
        
        val result = useCase(propertyId, false)
        
        assertTrue(result is AppResult.Success)
        coVerify { propertyRepository.likeProperty(userId, propertyId) }
        coVerify { engagementRepository.reportInteraction(propertyId, EngagementAction.LIKE) }
    }

    @Test
    fun `invoke should call unlikeProperty and NOT reportInteraction when currently liked`() = runTest {
        val userId = "user_1"
        val propertyId = "prop_1"
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { propertyRepository.unlikeProperty(userId, propertyId) } returns AppResult.Success(Unit)
        
        val result = useCase(propertyId, true)
        
        assertTrue(result is AppResult.Success)
        coVerify { propertyRepository.unlikeProperty(userId, propertyId) }
        coVerify(exactly = 0) { engagementRepository.reportInteraction(any(), any()) }
    }
}
