package com.estatia.realestate.apps.core.model.feature

/**
 * Represents the status of a payment transaction.
 */
enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED
}

/**
 * Represents the method used for payment.
 */
sealed class PaymentMethod {
    data object CreditCard : PaymentMethod()
    data object PayPal : PaymentMethod()
    data object ApplePay : PaymentMethod()
    data object GooglePay : PaymentMethod()
}
