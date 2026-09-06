package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.annotations.UseCase
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import javax.inject.Inject

interface IGetPropertyUseCase {
    suspend operator fun invoke(propertyId: String): AppResult<PropertyDomainModel>
}

@UseCase
class GetPropertyUseCase @Inject constructor(
    private val propertyRepository: IPropertyRepository
) : IGetPropertyUseCase {
    override suspend operator fun invoke(propertyId: String): AppResult<PropertyDomainModel> {
        return propertyRepository.getPropertyById(propertyId)
    }
}
