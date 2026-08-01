package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IPropertyRepository
import javax.inject.Inject

class TogglePropertyLikeUseCase @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(propertyId: String, isCurrentlyLiked: Boolean): AppResult<Unit> {
        val userId = authRepository.getCurrentUserId() ?: return AppResult.Error(AuthException.UserNotAuthenticated)
        
        return if (isCurrentlyLiked) {
            propertyRepository.unlikeProperty(userId, propertyId)
        } else {
            propertyRepository.likeProperty(userId, propertyId)
        }
    }
}
