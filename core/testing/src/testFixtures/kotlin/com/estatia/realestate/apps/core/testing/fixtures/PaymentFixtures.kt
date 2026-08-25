package com.estatia.realestate.apps.core.testing.fixtures

import com.estatia.realestate.apps.core.model.feature.PaymentMethod
import com.estatia.realestate.apps.core.model.property.Money
import kotlin.random.Random

/**
 * Unified source of truth for payment domain fixtures.
 */
object PaymentFixtures {

    /**
     * Returns a rich, deterministic payment amount.
     */
    fun defaultAmount() = Money(50000.0)

    /**
     * Returns a deterministic payment method.
     */
    fun defaultMethod() = PaymentMethod.CreditCard

    /**
     * Factory method for building randomized payment amounts.
     */
    fun buildAmount(min: Double = 1000.0, max: Double = 100000.0) = 
        Money(Random.nextDouble(min, max))

    /**
     * Factory method for building randomized payment methods.
     */
    fun buildMethod(): PaymentMethod = listOf(
        PaymentMethod.CreditCard,
        PaymentMethod.PayPal,
        PaymentMethod.ApplePay,
        PaymentMethod.GooglePay
    ).random()
}
