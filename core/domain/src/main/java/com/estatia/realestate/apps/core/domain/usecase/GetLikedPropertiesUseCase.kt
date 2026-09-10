package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.architecture.annotations.UseCase
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import com.estatia.realestate.apps.core.common.exceptions.getOrNull
import javax.inject.Inject
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IGetLikedPropertiesUseCase {
    suspend operator fun invoke(): AppResult<List<PropertyDomainModel>>
}

@UseCase
class GetLikedPropertiesUseCase @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    private val authRepository: IAuthRepository
) : IGetLikedPropertiesUseCase {
    override suspend operator fun invoke(): AppResult<List<PropertyDomainModel>> {
        val userId = authRepository.getCurrentUserId().getOrNull() 
            ?: return AppResult.Error(AuthException.UserNotAuthenticated)
        return propertyRepository.fetchLikedProperties(userId)
    }
}
