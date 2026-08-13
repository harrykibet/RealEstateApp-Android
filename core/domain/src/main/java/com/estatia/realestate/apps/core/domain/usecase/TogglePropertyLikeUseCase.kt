package com.estatia.realestate.apps.core.domain.usecase

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.AuthException
import com.estatia.realestate.apps.core.domain.interfaces.IAuthRepository
import com.estatia.realestate.apps.core.domain.interfaces.IEngagementRepository
import com.estatia.realestate.apps.core.domain.interfaces.IPropertyRepository
import com.estatia.realestate.apps.core.model.engagement.EngagementAction
import javax.inject.Inject

class TogglePropertyLikeUseCase @Inject constructor(
    private val propertyRepository: IPropertyRepository,
    private val authRepository: IAuthRepository,
    private val engagementRepository: IEngagementRepository
) {
    suspend operator fun invoke(propertyId: String, isCurrentlyLiked: Boolean): AppResult<Unit> {
        val userId = authRepository.getCurrentUserId() ?: return AppResult.Error(AuthException.UserNotAuthenticated)
        
        // 🏎️ Report engagement signal for personalized feed
        if (!isCurrentlyLiked) {
            engagementRepository.reportInteraction(propertyId, EngagementAction.LIKE)
        }

        return if (isCurrentlyLiked) {
            propertyRepository.unlikeProperty(userId, propertyId)
        } else {
            propertyRepository.likeProperty(userId, propertyId)
        }
    }
}
