package com.estatia.realestate.apps.core.model.feature

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Data.DomainModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

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
@DomainModel
sealed class PaymentMethod {
    @Serializable
@DomainModel
    data object CreditCard : PaymentMethod()
    @Serializable
@DomainModel
    data object PayPal : PaymentMethod()
    @Serializable
@DomainModel
    data object ApplePay : PaymentMethod()
    @Serializable
@DomainModel
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
@Contract
sealed interface PaymentResult {
    @Serializable
@DomainModel
    data class Success(val transactionId: String) : PaymentResult
    @Serializable
@DomainModel
    data class Failed(val reason: String) : PaymentResult
    @Serializable
@DomainModel
    data object Cancelled : PaymentResult
}
