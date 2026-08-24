package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.testing.assertions.assertError
import com.estatia.realestate.apps.core.testing.assertions.assertSuccess
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetLikedPropertiesUseCaseTest {

    private lateinit var propertyRepository: IPropertyRepository
    private lateinit var authRepository: IAuthRepository
    private lateinit var useCase: GetLikedPropertiesUseCase

    @Before
    fun setup() {
        propertyRepository = mockk()
        authRepository = mockk()
        useCase = GetLikedPropertiesUseCase(propertyRepository, authRepository)
    }

    @Test
    fun `invoke should return Error when user not authenticated using platform assertions`() = runTest {
        every { authRepository.getCurrentUserId() } returns null
        
        val result = useCase()
        
        val error = result.assertError()
        assertEquals(AuthException.UserNotAuthenticated, error)
    }

    @Test
    fun `invoke should return liked properties for authenticated user`() = runTest {
        val userId = "user_1"
        val mockProperties = listOf(mockk<PropertyDomainModel>())
        every { authRepository.getCurrentUserId() } returns userId
        coEvery { propertyRepository.fetchLikedProperties(userId) } returns AppResult.Success(mockProperties)
        
        val result = useCase()
        
        val data = result.assertSuccess()
        assertEquals(mockProperties, data)
    }
}
