package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.security.IAuthRepository
import com.estatia.realestate.apps.core.domain.repository.IPropertyRepository
import com.estatia.realestate.apps.core.model.property.PropertyDomainModel
import javax.inject.Inject

class GetLikedPropertiesUseCase @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(): AppResult<List<PropertyDomainModel>> {
        val userId = authRepository.getCurrentUserId() ?: return AppResult.Error(AuthException.UserNotAuthenticated)
        return propertyRepository.fetchLikedProperties(userId)
    }
}
