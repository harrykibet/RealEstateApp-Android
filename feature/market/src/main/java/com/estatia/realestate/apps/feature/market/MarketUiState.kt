package com.estatia.realestate.apps.feature.market

import com.estatia.realestate.apps.core.model.feature.MarketItem
import com.estatia.realestate.apps.core.model.feature.MarketProject

sealed interface MarketUiState {
    data object Loading : MarketUiState
    
    data class Success(
        val featuredServices: List<MarketItem>,
        val popularProducts: List<MarketItem>,
        val recommendedProfessionals: List<MarketItem>,
        val activeProjects: List<MarketProject> = emptyList()
    ) : MarketUiState
    
    data class Error(val message: String) : MarketUiState
}
