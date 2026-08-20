package com.estatia.realestate.apps.feature.market

import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.model.feature.MarketCategory
import com.estatia.realestate.apps.core.model.feature.MarketItem
import com.estatia.realestate.apps.core.model.feature.MarketProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MarketUiState>(MarketUiState.Loading)
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val services = listOf(
            MarketItem(
                id = "s1",
                title = "Professional Cleaning",
                description = "Deep cleaning for your new home.",
                category = MarketCategory.SERVICES,
                type = "Cleaning",
                price = 1500.0,
                priceUnit = "flat",
                isVerified = true,
                rating = 4.8f,
                reviewCount = 120,
                provider = MarketProvider("p1", "Sparkle Cleaners")
            ),
            MarketItem(
                id = "s2",
                title = "Emergency Plumbing",
                description = "Fixing leaks and burst pipes 24/7.",
                category = MarketCategory.SERVICES,
                type = "Plumbing",
                isVerified = true,
                rating = 4.9f,
                reviewCount = 85,
                provider = MarketProvider("p2", "John the Plumber")
            )
        )

        val products = listOf(
            MarketItem(
                id = "pr1",
                title = "Modern L-Shaped Sofa",
                description = "Comfortable 5-seater sofa for your living room.",
                category = MarketCategory.PRODUCTS,
                type = "Furniture",
                price = 45000.0,
                isVerified = true,
                rating = 4.5f,
                reviewCount = 30
            )
        )

        val professionals = listOf(
            MarketItem(
                id = "pro1",
                title = "Certified Valuer",
                description = "Get an accurate valuation for your property.",
                category = MarketCategory.PROFESSIONALS,
                type = "Valuation",
                isVerified = true,
                rating = 5.0f,
                reviewCount = 15,
                provider = MarketProvider("p3", "Safe Estates Valuation", verificationLevel = "PROFESSIONAL")
            )
        )

        _uiState.value = MarketUiState.Success(services, products, professionals)
    }
}
