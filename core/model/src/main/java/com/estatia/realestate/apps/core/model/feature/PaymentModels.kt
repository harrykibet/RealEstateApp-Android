package com.estatia.realestate.apps.core.model.feature

import kotlinx.serialization.Serializable

/**
 * Represents the status of a payment transaction.
 */
@Serializable
enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
}

/**
 * Represents the method used for payment.
 */
@Serializable
sealed class PaymentMethod {
    @Serializable
    data object CreditCard : PaymentMethod()
    @Serializable
    data object PayPal : PaymentMethod()
    @Serializable
    data object ApplePay : PaymentMethod()
    @Serializable
    data object GooglePay : PaymentMethod()
}

/**
 * Describes the context of the payment for presentation and analytics.
 */
@Serializable
enum class PaymentContext {
    BOOKING,
    LISTING_BOOST,
    SUBSCRIPTION
}

/**
 * Result of a payment flow, passed back to the calling feature.
 */
@Serializable
sealed interface PaymentResult {
    @Serializable
    data class Success(val transactionId: String) : PaymentResult
    @Serializable
    data class Failed(val reason: String) : PaymentResult
    @Serializable
    data object Cancelled : PaymentResult
}
