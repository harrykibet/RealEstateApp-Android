package com.estatia.realestate.apps.feature.market

import com.estatia.realestate.apps.core.model.feature.MarketItem
import com.estatia.realestate.apps.core.model.feature.MarketProject
import com.estatia.realestate.apps.core.architecture.annotations.Helper
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
sealed interface MarketUiState {
@Helper
    data object Loading : MarketUiState
    
@Helper
    data class Success(
        val featuredServices: List<MarketItem>,
        val popularProducts: List<MarketItem>,
        val recommendedProfessionals: List<MarketItem>,
        val activeProjects: List<MarketProject> = emptyList()
    ) : MarketUiState
    
@Helper
    data class Error(val message: String) : MarketUiState
}
