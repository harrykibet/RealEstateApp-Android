package com.estatia.realestate.apps.core.navigation.routes

import kotlinx.serialization.Serializable

@Serializable
data object PaymentBaseRoute

@Serializable
data class PaymentRoute(
    val paymentIntentId: String,
)