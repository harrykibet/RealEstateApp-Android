package com.estatia.realestate.apps.core.navigation.routes

import com.estatia.realestate.apps.core.model.feature.PaymentContext
import kotlinx.serialization.Serializable

@Serializable
data object PaymentBaseRoute

@Serializable
data class PaymentRoute(
    val referenceId: String,
    val amount: Double,
    val currency: String,
    val context: PaymentContext
)

object PaymentNavConstants {
    const val PAYMENT_RESULT_KEY = "payment_result"
}
