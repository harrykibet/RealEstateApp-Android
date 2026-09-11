package com.estatia.realestate.apps.core.navigation.routes

import com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility

import com.estatia.realestate.apps.core.model.feature.PaymentContext
import kotlinx.serialization.Serializable

@Serializable
@Utility
data object PaymentBaseRoute

@Serializable
@Utility
data class PaymentRoute(
    val referenceId: String,
    val amount: Double,
    val currency: String,
    val context: PaymentContext
)

@Utility
object PaymentNavConstants {
    const val PAYMENT_RESULT_KEY = "payment_result"
}
