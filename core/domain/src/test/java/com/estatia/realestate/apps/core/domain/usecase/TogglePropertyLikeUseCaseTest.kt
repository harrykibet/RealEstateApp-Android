package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import com.estatia.realestate.apps.core.testing.fake.analytics.FakeEngagementRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TogglePropertyLikeUseCaseTest {

    private lateinit var propertyRepository: IPropertyRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var engagementRepository: FakeEngagementRepository
    private lateinit var useCase: TogglePropertyLikeUseCase

    @Before
    fun setup() {
        propertyRepository = mockk()
        authRepository = mockk()
        engagementRepository = FakeEngagementRepository()
        useCase = TogglePropertyLikeUseCase(propertyRepository, authRepository, engagementRepository)
    }

    @Test
    fun `invoke should return Error when user not authenticated`() = runTest {
        every { authRepository.getCurrentUserId() } returns null
        
        val result = useCase("prop_1", false)
        
        val error = result.assertError()
        assertEquals(AuthException.UserNotAuthenticated, error)
    }

    @Test
    fun `invoke should call likeProperty and reportInteraction when not currently liked`() = runTest {
        val userId = "user_1"
        val propertyId = "prop_1"
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { propertyRepository.likeProperty(userId, propertyId) } returns AppResult.Success(Unit)
        
        useCase(propertyId, false).assertSuccess()
        
        // Verify via Witness in FakeEngagementRepository
        engagementRepository.witness.assertContains(
            FakeEngagementRepository.EngagementSignal.Interaction(propertyId, EngagementAction.LIKE)
        )
    }

    @Test
    fun `invoke should call unlikeProperty and NOT reportInteraction when currently liked`() = runTest {
        val userId = "user_1"
        val propertyId = "prop_1"
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { propertyRepository.unlikeProperty(userId, propertyId) } returns AppResult.Success(Unit)
        
        useCase(propertyId, true).assertSuccess()
        
        // Verify interaction was NOT recorded using getActions()
        val interactions = engagementRepository.witness.getActions().filterIsInstance<FakeEngagementRepository.EngagementSignal.Interaction>()
        assert(interactions.isEmpty())
    }
}
