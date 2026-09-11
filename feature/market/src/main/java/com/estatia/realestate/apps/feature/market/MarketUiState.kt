package com.estatia.realestate.apps.feature.market

import com.estatia.realestate.apps.core.model.feature.MarketItem
import com.estatia.realestate.apps.core.model.feature.MarketProject
import com.estatia.realestate.apps.core.architecture.annotations.Data.UiState
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
sealed interface MarketUiState {
@UiState
    data object Loading : MarketUiState
    
@UiState
    data class Success(
        val featuredServices: List<MarketItem>,
        val popularProducts: List<MarketItem>,
        val recommendedProfessionals: List<MarketItem>,
        val activeProjects: List<MarketProject> = emptyList()
    ) : MarketUiState
    
@UiState
    data class Error(val message: String) : MarketUiState
}
