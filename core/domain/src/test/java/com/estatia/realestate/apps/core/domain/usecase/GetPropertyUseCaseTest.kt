package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPropertyUseCaseTest {

    private lateinit var propertyRepository: IPropertyRepository
    private lateinit var useCase: GetPropertyUseCase

    @Before
    fun setup() {
        propertyRepository = mockk()
        useCase = GetPropertyUseCase(propertyRepository)
    }

    @Test
    fun `invoke should return property from repository`() = runTest {
        val propertyId = "prop_1"
        val mockProperty = mockk<PropertyDomainModel>()
        coEvery { propertyRepository.getPropertyById(propertyId) } returns AppResult.Success(mockProperty)
        
        val result = useCase(propertyId)
        
        assertTrue(result is AppResult.Success)
        assertEquals(mockProperty, (result as AppResult.Success).data)
    }
}
